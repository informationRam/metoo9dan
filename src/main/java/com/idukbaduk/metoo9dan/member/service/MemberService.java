package com.idukbaduk.metoo9dan.member.service;

import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.Member;

public interface MemberService {

    void createUser(Member member);
    void createUserWithEducatorInfo(Member member, EducatorInfo educatorInfo);

}
