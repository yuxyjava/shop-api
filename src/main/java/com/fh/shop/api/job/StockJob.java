package com.fh.shop.api.job;

import com.fh.shop.api.product.biz.IProductService;
import com.fh.shop.api.product.po.Product;
import com.fh.shop.api.util.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
public class StockJob {

    @Resource(name = "productService")
    private IProductService productService;
    @Autowired
    private MailUtil mailUtil;

    @Scheduled(cron = "0/20 * * * * ?")
    public void checkStock() {
        System.out.println("============"+new Date());
        // 获取库存不足的商品列表
        List<Product> stockLessProductList = productService.findStockLessProductList();
        // 生成表格
        StringBuffer productHtml = new StringBuffer();
        productHtml.append("<table bgcolor=\"#ffc0cb\" cellpadding=\"0\" cellspacing=\"0\" border=\"1px\" width=\"500px\">\n" +
                "    <thead>\n" +
                "        <tr>\n" +
                "            <th>商品名</th>\n" +
                "            <th>商品价格</th>\n" +
                "            <th>商品库存</th>\n" +
                "        </tr>\n" +
                "    </thead>\n" +
                "    <tbody>");
        for (Product product : stockLessProductList) {
            productHtml.append(" <tr>\n" +
                    "            <td>"+product.getProductName()+"</td>\n" +
                    "            <td>"+product.getPrice().toString()+"</td>\n" +
                    "            <td>"+product.getStock()+"</td>\n" +
                    "        </tr>");
        }
        productHtml.append("</tbody>\n" +
                "</table>");

        String tableHtml = productHtml.toString();
        // 发送邮件
        mailUtil.sendMail("532028476@qq.com", "库存不足提醒", tableHtml);
    }

}
