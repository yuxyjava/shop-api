package com.fh.shop.api.order.biz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fh.shop.api.cart.vo.Cart;
import com.fh.shop.api.cart.vo.CartItem;
import com.fh.shop.api.common.ResponseEnum;
import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.common.SystemConstant;
import com.fh.shop.api.config.MQConfig;
import com.fh.shop.api.exception.StockLessException;
import com.fh.shop.api.order.mapper.IOrderItemMapper;
import com.fh.shop.api.order.mapper.IOrderMapper;
import com.fh.shop.api.order.param.OrderParam;
import com.fh.shop.api.order.po.Order;
import com.fh.shop.api.order.po.OrderItem;
import com.fh.shop.api.order.vo.OrderConfirmVo;
import com.fh.shop.api.paylog.mapper.IPayLogMapper;
import com.fh.shop.api.paylog.po.PayLog;
import com.fh.shop.api.product.mapper.IProductMapper;
import com.fh.shop.api.recipient.biz.IRecipientService;
import com.fh.shop.api.recipient.mapper.IRecipientMapper;
import com.fh.shop.api.recipient.po.Recipient;
import com.fh.shop.api.util.KeyUtil;
import com.fh.shop.api.util.RedisUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("orderService")
public class IOrderServiceImpl implements IOrderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Resource(name="recipientService")
    private IRecipientService recipientService;
    @Autowired
    private IProductMapper productMapper;
    @Autowired
    private IRecipientMapper recipientMapper;
    @Autowired
    private IOrderMapper orderMapper;
    @Autowired
    private IOrderItemMapper orderItemMapper;
    @Autowired
    private IPayLogMapper payLogMapper;

    @Override
    public ServerResponse generateConfirmOrder(Long memberId) {
        // 获取会员对应的收件人列表
        List<Recipient> recipientList = recipientService.findList(memberId);
        // 获取会员对应的购物车信息
        String cartJson = RedisUtil.get(KeyUtil.buildCartKey(memberId));
        Cart cart = JSONObject.parseObject(cartJson, Cart.class);
        // 组装要返回的信息
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        orderConfirmVo.setCart(cart);
        orderConfirmVo.setRecipientList(recipientList);
        return ServerResponse.success(orderConfirmVo);
    }

    @Override
    public ServerResponse generateOrder(OrderParam orderParam) {
        Long memberId = orderParam.getMemberId();
        // 清除之前的标志位
        RedisUtil.delete(KeyUtil.buildOrderKey(memberId));
        RedisUtil.delete(KeyUtil.buildStockLessKey(memberId));
        RedisUtil.delete(KeyUtil.buildOrderErrorKey(memberId));
        // 将订单信息发送到消息队列中
        String orderParamJson = JSONObject.toJSONString(orderParam);
        rabbitTemplate.convertAndSend(MQConfig.ORDEREXCHANGE, MQConfig.ORDERROUTEKEY, orderParamJson);
        return ServerResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderParam orderParam) {
        Long memberId = orderParam.getMemberId();
        String cartJson = RedisUtil.get(KeyUtil.buildCartKey(memberId));
        Cart cart = JSONObject.parseObject(cartJson, Cart.class);
        List<CartItem> cartItemList = cart.getCartItemList();
        // 减库存[数据库的乐观锁]
        // update t_product set stock=stock-num where id=productId and stock >= num
        // 考虑到并发
        for (CartItem cartItem : cartItemList) {
            Long goodsId = cartItem.getGoodsId();
            int num = cartItem.getNum();
            int rowCount = productMapper.updateStock(goodsId, num);
            if (rowCount == 0) {
                // 没有更新成功,库存不足
                // 既要起到回滚的作用，又要起到提示的作用
                throw new StockLessException("stock less");
            }
        }
        // 获取对应的收件人信息
        Long recipientId = orderParam.getRecipientId();
        Recipient recipient = recipientMapper.selectById(recipientId);
        // 插入订单表
        Order order = new Order();
        // 手工设置id[通过雪花算法生成的唯一标识]
        String orderId = IdWorker.getIdStr();
        order.setId(orderId);
        order.setCreateTime(new Date());
        order.setRecipientor(recipient.getRecipientor());
        order.setPhone(recipient.getPhone());
        order.setAddress(recipient.getAddress());
        order.setUserId(memberId);
        BigDecimal totalPrice = cart.getTotalPrice();
        order.setTotalPrice(totalPrice);
        order.setRecipientId(recipientId);
        int payType = orderParam.getPayType();
        order.setPayType(payType);
        order.setStatus(SystemConstant.OrderStatus.WAIT_PAY); // 未支付
        order.setTotalNum(cart.getTotalNum());
        orderMapper.insert(order);
        // 插入订单明细表
        // 批量插入
        // insert into 表名 (字段1，字段2，...) values (值1， 值2，....),(值1，值2,....),(值1，值2,....)
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartItem cartItem : cartItemList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(cartItem.getGoodsId());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setProductName(cartItem.getGoodsName());
            orderItem.setUserId(memberId);
            orderItem.setImageUrl(cartItem.getImageUrl());
            orderItem.setNum(cartItem.getNum());
            orderItem.setSubPrice(cartItem.getSubPrice());
            orderItemList.add(orderItem);
        }
        // 批量插入订单明细表
        orderItemMapper.batchInsert(orderItemList);
        // 插入支付日志表【下订单】
        PayLog payLog = new PayLog();
        String outTradeNo = IdWorker.getIdStr();
        payLog.setOutTradeNo(outTradeNo);
        payLog.setPayMoney(totalPrice);
        payLog.setUserId(memberId);
        payLog.setCreateTime(new Date());
        payLog.setOrderId(orderId);
        payLog.setPayStatus(SystemConstant.PayStatus.WAIT_PAY); // 等待支付
        payLog.setPayType(payType);
        payLogMapper.insert(payLog);
        // 将支付日志存入redis
        String paylogJson = JSONObject.toJSONString(payLog);
        RedisUtil.set(KeyUtil.buildPayLogKey(memberId), paylogJson);
        // 删除购物车中的信息
        RedisUtil.delete(KeyUtil.buildCartKey(memberId));
        // 提交订单成功
        RedisUtil.set(KeyUtil.buildOrderKey(memberId), "ok");
    }

    @Override
    public ServerResponse getResult(Long memberId) {
       if (RedisUtil.exist(KeyUtil.buildStockLessKey(memberId))) {
           RedisUtil.delete(KeyUtil.buildStockLessKey(memberId));
           // 证明库存不足
           return ServerResponse.error(ResponseEnum.ORDER_STOCK_LESS);
       }
       if (RedisUtil.exist(KeyUtil.buildOrderKey(memberId))) {
           // 删除redis中的订单成功的标志位
           RedisUtil.delete(KeyUtil.buildOrderKey(memberId));
           // 证明下订单成功
           return ServerResponse.success();
       }
       if (RedisUtil.exist(KeyUtil.buildOrderErrorKey(memberId))) {
           RedisUtil.delete(KeyUtil.buildOrderErrorKey(memberId));
           // 证明下单失败
           return ServerResponse.error(ResponseEnum.ORDER_IS_ERROR);
       }
       return ServerResponse.error(ResponseEnum.ORDER_IS_QUEUE);
    }
}
