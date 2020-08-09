package com.fh.shop.api.cate.controller;

import com.fh.shop.api.annotation.Check;
import com.fh.shop.api.cate.biz.ICateService;
import com.fh.shop.api.common.ServerResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/cate")
public class CateController {

    @Resource(name="cateService")
    private ICateService cateService;

    @GetMapping("/list")
    public ServerResponse list() {
        return cateService.findCateList();
    }
}
