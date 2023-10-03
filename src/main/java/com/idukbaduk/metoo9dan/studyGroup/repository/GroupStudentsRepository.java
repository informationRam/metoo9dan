package com.idukbaduk.metoo9dan.studyGroup.repository;

import com.idukbaduk.metoo9dan.common.entity.GroupStudents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupStudentsRepository extends JpaRepository<GroupStudents,Integer> {
}
