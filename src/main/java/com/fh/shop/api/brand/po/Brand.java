package com.fh.shop.api.brand.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("品牌描述")
public class Brand implements Serializable {

    @ApiModelProperty(name = "id", value = "品牌id", required = true)
    private Long id;
    @ApiModelProperty(name = "brandName", value = "品牌名", required = true)
    private String brandName;
}
