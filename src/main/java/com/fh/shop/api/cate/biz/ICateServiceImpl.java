package com.fh.shop.api.cate.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fh.shop.api.cate.mapper.ICateMapper;
import com.fh.shop.api.cate.po.Category;
import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("cateService")
public class ICateServiceImpl implements ICateService {

    @Autowired
    private ICateMapper cateMapper;

    @Override
    public ServerResponse findCateList() {
        String cateListJson = RedisUtil.get("cateList");
        if (StringUtils.isNotEmpty(cateListJson)) {
            List<Category> categories = JSONArray.parseArray(cateListJson, Category.class);
            RedisUtil.expire("cateList", 1*60);
            return ServerResponse.success(categories);
        }
        List<Category> categories = cateMapper.selectList(null);
        String cateArrJson = JSONObject.toJSONString(categories);
        RedisUtil.setEx("cateList", cateArrJson, 1*60);
        return ServerResponse.success(categories);
    }
}
