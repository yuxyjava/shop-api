package com.fh.shop.api;

import com.fh.shop.api.util.RedisUtil;
import org.junit.Test;

public class TestRedis {

    @Test
    public void test1() {
//        RedisUtil.set("userName", "zhangsan");
//
//        String userName = RedisUtil.get("userName");
//        System.out.println(userName);
        RedisUtil.delete("userName");
    }
}
