package com.idukbaduk.metoo9dan.homework.repository;

import com.idukbaduk.metoo9dan.common.entity.GroupStudents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HwGroupStudentsRepository extends JpaRepository<GroupStudents, Integer> {
    List<GroupStudents> findByIsApprovedAndStudyGroupsGroupNo(Boolean isApproved, Integer groupNo);
}
