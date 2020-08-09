package com.fh.shop.api.member.controller;

import com.fh.shop.api.annotation.Check;
import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.common.SystemConstant;
import com.fh.shop.api.member.biz.IMemberService;
import com.fh.shop.api.member.mapper.IMemberMapper;
import com.fh.shop.api.member.po.Member;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.api.util.KeyUtil;
import com.fh.shop.api.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/members")
@Api(tags = "会员接口")
public class MemberController {

    @Resource(name="memberService")
    private IMemberService memberService;

    @PostMapping
    @ApiOperation("会员注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name="memberName", value = "会员名", type = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name="password", value = "密码", type = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name="realName", value = "真实姓名", type = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name="birthday", value = "生日", type = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name="mail", value = "邮箱", type = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name="phone", value = "手机", type = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name="shengId", value = "省id", type = "long", required = false, paramType = "query"),
            @ApiImplicitParam(name="shiId", value = "市id", type = "long", required = false, paramType = "query"),
            @ApiImplicitParam(name="xianId", value = "县id", type = "long", required = false, paramType = "query"),
            @ApiImplicitParam(name="areaName", value = "地区名", type = "string", required = false, paramType = "query")
    })
    public ServerResponse add(Member member) {
        return memberService.addMember(member);
    }

    @GetMapping("/validateMemberName")
    public ServerResponse validateMemberName(String memberName) {
        return memberService.validateMemberName(memberName);
    }

    @GetMapping("/validatePhone")
    public ServerResponse validatePhone(String phone) {
        return memberService.validatePhone(phone);
    }

    @GetMapping("/validateMail")
    public ServerResponse validateMail(String mail) {
        return memberService.validateMail(mail);
    }

    @PostMapping("/login")
    @ApiOperation("会员登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "会员名", type = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", type = "string", required = true, paramType = "query")
    })
    public ServerResponse login(String userName, String password) {
        return memberService.login(userName, password);
    }

    @GetMapping("/findMember")
    @Check
    @ApiOperation("获取会员信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth", value = "头信息", required = true, type = "string", paramType = "header")
    })
    public ServerResponse findMember(HttpServletRequest request) {
        MemberVo member = (MemberVo) request.getAttribute(SystemConstant.CURR_MEMBER);
        return ServerResponse.success(member);
    }

    @GetMapping("/logout")
    @Check
    public ServerResponse logout(HttpServletRequest request) {
        MemberVo member = (MemberVo) request.getAttribute(SystemConstant.CURR_MEMBER);
        Long memberId = member.getId();
        String uuid = member.getUuid();
        // 清除redis中的数据
        RedisUtil.delete(KeyUtil.buildMemberKey(uuid, memberId));
        return ServerResponse.success();
    }
}
