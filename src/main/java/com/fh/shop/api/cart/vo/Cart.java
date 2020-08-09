package com.fh.shop.api.cart.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fh.shop.api.util.BigDecimalJackson;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class Cart implements Serializable {

    @JsonSerialize(using = BigDecimalJackson.class)
    private BigDecimal totalPrice;

    private int totalNum;

    private List<CartItem> cartItemList = new ArrayList<>();
}
