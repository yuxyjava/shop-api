package com.fh.shop.api.member.biz;

import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.member.po.Member;

public interface IMemberService {

    public ServerResponse addMember(Member member);

    ServerResponse validateMemberName(String memberName);

    ServerResponse validatePhone(String phone);

    ServerResponse validateMail(String mail);

    ServerResponse login(String userName, String password);
}
