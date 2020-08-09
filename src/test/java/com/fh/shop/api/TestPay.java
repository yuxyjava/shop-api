package com.fh.shop.api;

import com.fh.shop.api.config.WXConfig;
import com.github.wxpay.sdk.WXPay;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class TestPay {

    @Test
    public void test2() throws Exception {
        WXConfig config = new WXConfig();
        WXPay wxpay = new WXPay(config);

        Map<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", "23221111QQQQ");

        try {
            Map<String, String> resp = wxpay.orderQuery(data);
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws Exception {
        WXConfig config = new WXConfig();
        WXPay wxpay = new WXPay(config);

        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "飞狐乐购888-订单支付");
        // 订单号
        data.put("out_trade_no", "23221111QQQQ");
        // 要把项目中的元转为分 （元*100）
        data.put("total_fee", "10");
        data.put("notify_url", "http://it.feihu.com");
        data.put("trade_type", "NATIVE");  // 此处指定为扫码支付
        try {
            Map<String, String> resp = wxpay.unifiedOrder(data);
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
