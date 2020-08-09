package com.fh.shop.api.brand.controller;

import com.fh.shop.api.brand.biz.IBrandService;
import com.fh.shop.api.brand.po.Brand;
import com.fh.shop.api.common.ServerResponse;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/brands")
@Api(tags = "品牌操作相关的接口")
public class BrandController {

    @Resource(name="brandService")
    private IBrandService brandService;

    @PostMapping
    @ApiOperation("添加品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandName", value = "品牌名", type = "string", required = true, paramType = "query")
    })
    public ServerResponse add(Brand brand) {
        return brandService.addBrand(brand);
    }

    @GetMapping
    @ApiOperation("获取所有品牌列表")
    public ServerResponse list() {
        return brandService.findList();
    }

    @DeleteMapping("/{brandId}")
    @ApiOperation("根据品牌id删除指定的品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandId", value = "品牌id", required = true, type = "long", paramType = "path")
    })
    public ServerResponse delete(@PathVariable("brandId") Long id) {
        return brandService.delete(id);
    }

    @PutMapping
    @ApiOperation("更新品牌")
    public ServerResponse update(@ApiParam(value = "品牌的Json格式", required = true) @RequestBody Brand brand) {
        return brandService.update(brand);
    }

    @DeleteMapping
    @ApiOperation("批量删除品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "品牌id集合，逗号分隔，如1,2,3", type = "string", required = true, paramType = "query")
    })
    public ServerResponse deleteBatch(String ids) {
        return brandService.deleteBatch(ids);
    }


}
