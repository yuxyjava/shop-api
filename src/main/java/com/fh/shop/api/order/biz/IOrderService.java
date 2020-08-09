package com.fh.shop.api.order.biz;

import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.order.param.OrderParam;

public interface IOrderService {

    public ServerResponse generateConfirmOrder(Long memberId);

    ServerResponse generateOrder(OrderParam orderParam);

    void createOrder(OrderParam orderParam);

    ServerResponse getResult(Long memberId);
}
