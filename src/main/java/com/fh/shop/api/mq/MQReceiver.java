package com.fh.shop.api.mq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.api.cart.vo.Cart;
import com.fh.shop.api.cart.vo.CartItem;
import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.config.MQConfig;
import com.fh.shop.api.exception.StockLessException;
import com.fh.shop.api.order.biz.IOrderService;
import com.fh.shop.api.order.param.OrderParam;
import com.fh.shop.api.product.mapper.IProductMapper;
import com.fh.shop.api.product.po.Product;
import com.fh.shop.api.util.KeyUtil;
import com.fh.shop.api.util.MailUtil;
import com.fh.shop.api.util.RedisUtil;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MQReceiver {

    @Autowired
    private MailUtil mailUtil;
    @Autowired
    private IProductMapper productMapper;
    @Resource(name = "orderService")
    private IOrderService orderService;

    @RabbitListener(queues = MQConfig.MAILQUEUE)
    public void handleMailMessage(String msg, Message message, Channel channel) throws IOException {
        MailMessage mailMessage = JSONObject.parseObject(msg, MailMessage.class);
        String mail = mailMessage.getMail();
        String title = mailMessage.getTitle();
        String content = mailMessage.getContent();
        mailUtil.sendMail(mail, title, content);
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        channel.basicAck(deliveryTag, false);
    }

    @RabbitListener(queues = MQConfig.ORDERQUUE)
    public void handleOrderMessage(String msg, Message message, Channel channel) throws IOException {
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        OrderParam orderParam = JSONObject.parseObject(msg, OrderParam.class);
        Long memberId = orderParam.getMemberId();
        // 获取redis中的购物车信息
        String cartJson = RedisUtil.get(KeyUtil.buildCartKey(memberId));
        Cart cart = JSONObject.parseObject(cartJson, Cart.class);
        // 购买的商品的数量和数据库中对应的商品做对比，发现库存不足，进行提醒
        if (cart == null) {
            System.out.println("购物车为空===========");
            // 把对应的消息从消息队列中删除了[手动ack]
            channel.basicAck(deliveryTag, false);
            return;
        }
        List<CartItem> cartItemList = cart.getCartItemList();
        List<Long> goodIdList = cartItemList.stream().map(x -> x.getGoodsId()).collect(Collectors.toList());
        // 根据id集合从数据库中查找对应的商品列表
        QueryWrapper<Product> productQueryWrapper = new QueryWrapper<>();
        productQueryWrapper.in("id", goodIdList);
        List<Product> productList = productMapper.selectList(productQueryWrapper);
        // 循环对比，看库存是否充足
        for (CartItem cartItem : cartItemList) {
            for (Product product : productList) {
                if (cartItem.getGoodsId().longValue() == product.getId().longValue()) {
                    if (cartItem.getNum() > product.getStock()) {
                        // 提醒库存不足
                        RedisUtil.set(KeyUtil.buildStockLessKey(memberId), "stock less");
                        // 把对应的消息从消息队列中删除了[手动ack]
                        channel.basicAck(deliveryTag, false);
                        // 返回
                        return;
                    }
                }
            }
        }
        try {
            // 创建订单
            orderService.createOrder(orderParam);
            // 把对应的消息从消息队列中删除了[手动ack]
            channel.basicAck(deliveryTag, false);
        } catch (StockLessException e) {
            e.printStackTrace();
            // 库存不足
            // 提醒库存不足
            RedisUtil.set(KeyUtil.buildStockLessKey(memberId), "stock less");
            // 把对应的消息从消息队列中删除了[手动ack]
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            // 下订单失败
            RedisUtil.set(KeyUtil.buildOrderErrorKey(memberId), "error");
            // 把对应的消息从消息队列中删除了[手动ack]
            channel.basicAck(deliveryTag, false);
        }
    }
}
