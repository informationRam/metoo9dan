package com.idukbaduk.metoo9dan.member.service;

import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.dto.MemberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MemberService {

    //페이징
    Page<Member> findAllMembers(Pageable pageable);
    //회원삭제-단일
    void deleteMemberByMemberNo(Integer memberNo);
    //다중회원 삭제
    void deleteAllMembers(List<Integer> memberNos);
    //회원번호로 자격 가져오기
    String getMembershipStatusByMemberNo(Integer memberNo);
    //회원번호로 회원 정보 가져오기
    MemberDTO getMemberByMemberNo(Integer memberNo);

    //회원정보전체 조회
    List<Member> getAllMembers();
    //교육자회원가입-정보추가
    void createUserWithEducatorInfo(Member member, EducatorInfo educatorInfo);

    //회원가입
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

    //휴대폰 중복여부 확인
    boolean checkmemberTelDuplication(String tel);

    //아이디 찾기 - 이메일로 찾기
    String searchId(String email);

    //비밀번호찾기 - id & email값 동시에 일치하는 회원이 있는지
    boolean searchPwd(String memberId,String email);

    // 임시 비번 변경
    void userPwdModify(Member member, String tempPassword);

    boolean updateMemberData(Integer memberNo, MemberDTO updatedMemberData);
}
