package com.fh.shop.api.product.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductVo implements Serializable {

    private Long id;

    private String productName;

    private String mainImagePath;

    private String price;
}
