package com.fh.shop.api.pay.biz;

import com.fh.shop.api.common.ServerResponse;

public interface IPayService {

    public ServerResponse createNative(Long mebmerId);

    ServerResponse queryStatus(Long memberId);
}
