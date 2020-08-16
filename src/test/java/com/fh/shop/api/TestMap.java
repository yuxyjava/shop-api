package com.fh.shop.api;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestMap {

    @Test
    public void test1() {
        Map<String, String> infoMap = new LinkedHashMap<>();
        infoMap.put("name", "zhangsan");
        infoMap.put("age", "30");
        infoMap.put("sex", "xx");

        Iterator<Map.Entry<String, String>> iterator = infoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            System.out.println(next.getKey()+":"+next.getValue());
        }
    }
}
