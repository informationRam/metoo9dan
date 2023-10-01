package com.idukbaduk.metoo9dan.studyGroup.repository;

import com.idukbaduk.metoo9dan.common.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member,Integer> {
}
