package com.fh.shop.api.cate.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class Category implements Serializable {

    private Long id;

    private String categoryName;

    private Long fatherId;

    private Long typeId;

    private String typeName;

}
