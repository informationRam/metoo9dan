package com.idukbaduk.metoo9dan.member.repository;


import com.idukbaduk.metoo9dan.common.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
/*import org.springframework.security.core.userdetails.User;*/
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    //아이디로 값 찾기
    Optional<Member> findByMemberId(String memberId);

    //회원가입시 중복값 아이디 확인
    boolean existsBymemberId(String memberId);

    //회원가입시 중복값 이메일 확인
    boolean existsByEmail(String email);

    //이메일로 아이디 찾기
    Optional<Member> findByEmail(String email);

    //비밀번호 찾기 - 아이디 && 이메일주소
    Optional<Member> findBymemberIdAndEmail(String memberId, String email);


}