package com.idukbaduk.metoo9dan.studyGroup.repository;

import com.idukbaduk.metoo9dan.common.entity.GroupStudents;
import com.idukbaduk.metoo9dan.common.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupStudentsRepository extends JpaRepository<GroupStudents,Integer> {
    Optional<GroupStudents> findByMemberAndIsApprovedIsFalse(Member member);
}
