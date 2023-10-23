package com.idukbaduk.metoo9dan.member.repository;

import com.idukbaduk.metoo9dan.common.entity.GroupStudents;
import com.idukbaduk.metoo9dan.common.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupStudentsRepository extends JpaRepository<GroupStudents, Integer> {
    Optional<GroupStudents> findByMemberAndIsApprovedIsFalse(Member member);
}
