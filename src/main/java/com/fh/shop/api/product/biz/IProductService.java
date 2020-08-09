package com.fh.shop.api.product.biz;

import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.product.po.Product;

import java.util.List;

public interface IProductService {

    public ServerResponse findHotList();

    public List<Product> findStockLessProductList();
}
