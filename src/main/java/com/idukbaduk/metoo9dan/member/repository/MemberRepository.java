package com.idukbaduk.metoo9dan.member.repository;


import com.idukbaduk.metoo9dan.common.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    //아이디로 값 찾기
    Member findByMemberId (String memberId);

}