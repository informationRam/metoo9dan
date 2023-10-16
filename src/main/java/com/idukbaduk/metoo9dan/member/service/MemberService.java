package com.idukbaduk.metoo9dan.member.service;

import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.Member;
import org.springframework.transaction.annotation.Transactional;

public interface MemberService {

    void createUserWithEducatorInfo(Member member, EducatorInfo educatorInfo);

    void createUser(Member member);

    //id로 회원정보 가져오기
    Member getUser(String memberId);
    //이메일 넣어 회원정보 찾기
    Member getUserbyEmail (String email);

    //회원가입 이메일 중복 확인
    boolean checkEmailDuplication(String email);

    //회원 정보 수정
    boolean checkEmailDuplication(Member member, MemberModifyForm memberModifyForm);

    //아이디 중복여부 확인
    boolean checkmemberIdDuplication(String user_id);

    //아이디 찾기 - 이메일로 찾기
    String searchId(String email);

    //비밀번호찾기 - id & email값 동시에 일치하는 회원이 있는지
    boolean searchPwd(String memberId,String email);

    // 임시 비번 변경
    void userPwdModify(Member member, String tempPassword);

}