package com.fh.shop.api.common;

public enum ResponseEnum {

    ORDER_STOCK_LESS(4000, "下订单时，库存不足"),
    ORDER_IS_QUEUE(4001, "订单正在排队"),
    ORDER_IS_ERROR(4002, "下单失败"),

    PAY_IS_FAIL(5000, "支付失败,超时了"),

    TOKEN_IS_MISS(6000, "token头信息丢失"),
    TOEKN_IS_ERROR(6001, "token错误"),
    TOKEN_REQUEST_REPET(6002, "请求重复"),


    CART_PRODUCT_IS_NULL(3000, "添加的商品不存在"),
    CART_PRODUCT_IS_DOWN(3001, "商品下架了"),
    CART_NUM_IS_ERROR(3002, "商品的数量不合法"),
    CART_DELETE_BATCH_IDS_IS_NULL(3003, "批量删除时ids必须传递"),

    LOGIN_INFO_IS_NULL(2000, "用户名或密码为空"),
    LOGIN_MEMBER_NAME_IS_NOT_EXIT(2001, "会员名不存在"),
    LOGIN_PASSWORD_IS_ERROR(2002, "密码错误"),
    LOGIN_HEADER_IS_MISS(2003, "头信息丢失"),
    LOGIN_HEADER_CONTENT_IS_MISS(2004, "头信息不完整"),
    LOGIN_MEMBER_IS_CHANGE(2005, "会员信息被篡改"),
    LOGIN_TIME_OUT(2006, "登录超时"),

    REG_MEMBER_IS_EXIST(1001, "会员已经存在了"),
    REG_PHONE_IS_EXIST(1002, "手机号已经存在了"),
    REG_MAIL_IS_EXIST(1003, "邮箱已经存在了"),
    REG_MEMBER_IS_NULL(1000, "注册会员信息为空了");

    private int code;
    private String msg;

    private ResponseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
