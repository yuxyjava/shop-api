package com.fh.shop.api;

import com.fh.shop.api.brand.biz.IBrandService;
import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.mq.MQSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ShopApiApplicationTests {

    @Resource(name="brandService")
    private IBrandService brandService;
    @Autowired
    private MQSender mqSender;

    @Test
    void contextLoads() {
    }

    @Test
    public void testSend() {
        ServerResponse list = brandService.findList();
        System.out.println(list);
    }

    @Test
    public void testSendMsg() {
        mqSender.sendMail("你好！！！");
    }

    @Test
    public void test2() {
        mqSender.sendMsg1("飞狐你好！！！");
    }

    @Test
    public void test3() {
        mqSender.sendMsg2("天道酬勤！！！");
    }






}
