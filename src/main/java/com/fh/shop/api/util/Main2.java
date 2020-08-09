package com.fh.shop.api.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main2 {

    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        names.add("张三");
        names.add("李四");
        names.add("王五");

        Iterator<String> iterator = names.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.equals("李四")) {
                iterator.remove();
            }
        }
        System.out.println(names);
    }
}
