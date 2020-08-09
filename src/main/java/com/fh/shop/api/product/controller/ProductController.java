package com.fh.shop.api.product.controller;

import com.fh.shop.api.annotation.Check;
import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.product.biz.IProductService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Resource(name="productService")
    private IProductService productService;

    @GetMapping("/findHotList")
    public ServerResponse findHotList() {
        return productService.findHotList();
    }
}
