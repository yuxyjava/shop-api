package com.fh.shop.api.product.biz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.product.mapper.IProductMapper;
import com.fh.shop.api.product.po.Product;
import com.fh.shop.api.product.vo.ProductVo;
import com.fh.shop.api.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("productService")
public class IProductServiceImpl implements IProductService {

    @Value("${stock.less}")
    private int stockLess;

    @Autowired
    private IProductMapper productMapper;

    @Override
    public ServerResponse findHotList() {
        // 从缓存中取数据
        String hotProductList = RedisUtil.get("hotProductList");
        if (StringUtils.isNotEmpty(hotProductList)) {
            // 将json格式的字符串转为java对象
            // [如果要转为java中的集合则用parseArray]
            // [如果要转换为java中的对象则用parseObject]
            List<ProductVo> productList = JSONObject.parseArray(hotProductList, ProductVo.class);
            return ServerResponse.success(productList);
        }
        QueryWrapper<Product> productQueryWrapper = new QueryWrapper<>();
        productQueryWrapper.select("id", "productName", "price", "mainImagePath");
        productQueryWrapper.eq("isHot", 1);
        productQueryWrapper.eq("status", 1);
        // 查询数据库对应的是po
        List<Product> productList = productMapper.selectList(productQueryWrapper);
        // po转vo
        List<ProductVo> productVoList = new ArrayList<>();
        for (Product product : productList) {
            ProductVo productVo = new ProductVo();
            productVo.setId(product.getId());
            productVo.setProductName(product.getProductName());
            productVo.setPrice(product.getPrice().toString());
            productVo.setMainImagePath(product.getMainImagePath());
            productVoList.add(productVo);
        }
        // 往缓存中存一份
        // 把java对象转换为Json格式的字符串
        String hotProductListJson = JSONObject.toJSONString(productVoList);
        // 定时刷新，设置的缓存，指定过期时间
        RedisUtil.setEx("hotProductList", hotProductListJson, 10*60);
        return ServerResponse.success(productVoList);
    }

    @Override
    public List<Product> findStockLessProductList() {
        QueryWrapper<Product> productQueryWrapper = new QueryWrapper<>();
        productQueryWrapper.lt("stock", stockLess);
        List<Product> productList = productMapper.selectList(productQueryWrapper);
        return productList;
    }
}
