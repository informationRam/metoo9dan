package com.idukbaduk.metoo9dan.studyGroup.repository;

import com.idukbaduk.metoo9dan.common.entity.StudyGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//학습 그룹
@Repository
public interface GroupRepository extends JpaRepository<StudyGroups,Integer> {
}
