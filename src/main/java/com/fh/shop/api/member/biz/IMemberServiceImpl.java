package com.fh.shop.api.member.biz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.api.common.ResponseEnum;
import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.member.mapper.IMemberMapper;
import com.fh.shop.api.member.po.Member;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.api.mq.MQSender;
import com.fh.shop.api.mq.MailMessage;
import com.fh.shop.api.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.provider.MD5;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service("memberService")
@Transactional(rollbackFor = Exception.class)
public class IMemberServiceImpl implements IMemberService {

    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private MailUtil mailUtil;
    @Autowired
    private MQSender mqSender;

    @Override
    public ServerResponse addMember(Member member) {
        // 验证逻辑
        // 非空判断
        String memberName = member.getMemberName();
        String password = member.getPassword();
        String mail = member.getMail();
        String phone = member.getPhone();
        if (StringUtils.isEmpty(memberName) || StringUtils.isEmpty(password)
                                            || StringUtils.isEmpty(mail)
                                            || StringUtils.isEmpty(phone)) {
            return ServerResponse.error(ResponseEnum.REG_MEMBER_IS_NULL);
        }
        // 判断会员名是否唯一
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("memberName", memberName);
        Member memberDB = memberMapper.selectOne(queryWrapper);
        if (memberDB != null) {
            return ServerResponse.error(ResponseEnum.REG_MEMBER_IS_EXIST);
        }
        // 判断手机号是否唯一
        QueryWrapper<Member> phoneQueryWrapper = new QueryWrapper<>();
        phoneQueryWrapper.eq("phone", phone);
        Member member1 = memberMapper.selectOne(phoneQueryWrapper);
        if (member1 != null) {
            return ServerResponse.error(ResponseEnum.REG_PHONE_IS_EXIST);
        }
        // 判断邮箱是否唯一
        QueryWrapper<Member> mailQueryWrapper = new QueryWrapper<>();
        mailQueryWrapper.eq("mail", mail);
        Member member2 = memberMapper.selectOne(mailQueryWrapper);
        if (member2 != null) {
            return ServerResponse.error(ResponseEnum.REG_MAIL_IS_EXIST);
        }
        // 注册会员
        memberMapper.addMember(member);
        // 给注册的会员发邮件
        mailUtil.sendMail(mail, "注册成功", "恭喜"+member.getRealName()+"注册成功！！！");
        return ServerResponse.success();
    }

    @Override
    public ServerResponse validateMemberName(String memberName) {
        if (StringUtils.isEmpty(memberName)) {
            return ServerResponse.error(ResponseEnum.REG_MEMBER_IS_NULL);
        }
        // 进行唯一性验证
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("memberName", memberName);
        Member memberDB = memberMapper.selectOne(queryWrapper);
        if (memberDB != null) {
            return ServerResponse.error(ResponseEnum.REG_MEMBER_IS_EXIST);
        }
        return ServerResponse.success();
    }

    @Override
    public ServerResponse validatePhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return ServerResponse.error(ResponseEnum.REG_MEMBER_IS_NULL);
        }
        QueryWrapper<Member> phoneQueryWrapper = new QueryWrapper<>();
        phoneQueryWrapper.eq("phone", phone);
        Member member1 = memberMapper.selectOne(phoneQueryWrapper);
        if (member1 != null) {
            return ServerResponse.error(ResponseEnum.REG_PHONE_IS_EXIST);
        }
        return ServerResponse.success();
    }

    @Override
    public ServerResponse validateMail(String mail) {
        if (StringUtils.isEmpty(mail)) {
            return ServerResponse.error(ResponseEnum.REG_MEMBER_IS_NULL);
        }
        QueryWrapper<Member> mailQueryWrapper = new QueryWrapper<>();
        mailQueryWrapper.eq("mail", mail);
        Member member2 = memberMapper.selectOne(mailQueryWrapper);
        if (member2 != null) {
            return ServerResponse.error(ResponseEnum.REG_MAIL_IS_EXIST);
        }
        return ServerResponse.success();
    }

    @Override
    public ServerResponse login(String userName, String password) {
        // 非空判断
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return ServerResponse.error(ResponseEnum.LOGIN_INFO_IS_NULL);
        }
        // 解密
        userName = RSAUtil.decrypt(userName);
        password = RSAUtil.decrypt(password);
        // 判断用户名是否存在
        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
        memberQueryWrapper.eq("memberName", userName);
        Member member = memberMapper.selectOne(memberQueryWrapper);
        if (member == null) {
            return ServerResponse.error(ResponseEnum.LOGIN_MEMBER_NAME_IS_NOT_EXIT);
        }
        // 判断密码是否正确
        if (!password.equals(member.getPassword())) {
            return ServerResponse.error(ResponseEnum.LOGIN_PASSWORD_IS_ERROR);
        }
        // ============生成token================
        // 模拟JWT[Json Web Token]
        // 生成token样子类似于xxx.yyy 用户信息.对用户信息的签名
        // 签名的目的:保证用户信息不被篡改
        // 怎么生成签名:md5(用户信息 结合 秘钥)
        // sign代表签名 secret/secretKey代表秘钥
        // 秘钥是在服务端保存的，黑客，攻击者它们获取不到
        //=======================================
        // 生成 用户信息 对应的 json
        MemberVo memberVo = new MemberVo();
        Long memberId = member.getId();
        memberVo.setId(memberId);
        memberVo.setMemberName(member.getMemberName());
        memberVo.setRealName(member.getRealName());
        String uuid = UUID.randomUUID().toString();
        memberVo.setUuid(uuid);
        // 转换java对象到json
        String memberJson = JSONObject.toJSONString(memberVo);
        // 对用户信息进行base64编码
        // [起到了一定的安全作用]
        // (对于计算机小白来说，唬他了一下，但是对于计算机专业人事来说，起不到任何作用，可以直接将base64解码)
        // jdk1.8内部直接提供了base64的工具类，如果jdk的版本低于1.8,就需要使用第三方来完成base64编码
        try {
            String memberJsonBase64 = Base64.getEncoder().encodeToString(memberJson.getBytes("utf-8"));
            // 生成用户信息所对应的签名
            String sign = Md5Util.sign(memberJsonBase64, Md5Util.SECRET);
            // 对签名也进行base64编码
            String signBase64 = Base64.getEncoder().encodeToString(sign.getBytes("utf-8"));
            // 处理超时
            RedisUtil.setEx(KeyUtil.buildMemberKey(uuid, memberId), "", KeyUtil.MEMBER_KEY_EXPIRE);
            // 发送邮件
            String mail = member.getMail();
//            mailUtil.sendMail(mail, "登录成功", member.getRealName()+"在"+DateUtil.date2str(new Date(), DateUtil.FULL_TIME)+"登录了！！！");
            MailMessage mailMessage = new MailMessage();
            mailMessage.setMail(mail);
            mailMessage.setTitle("登录成功");
            mailMessage.setRealName(member.getRealName());
            mailMessage.setContent(member.getRealName()+"在"+DateUtil.date2str(new Date(), DateUtil.FULL_TIME)+"登录了！！！");
            mqSender.sendMailMessage(mailMessage);
            // 响应数据给客户端
            return ServerResponse.success(memberJsonBase64+"."+signBase64);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
            return ServerResponse.error();
        }
    }
}
