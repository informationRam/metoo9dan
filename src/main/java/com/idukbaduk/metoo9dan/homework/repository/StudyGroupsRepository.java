package com.idukbaduk.metoo9dan.homework.repository;

import com.idukbaduk.metoo9dan.common.entity.HomeworkSend;
import com.idukbaduk.metoo9dan.common.entity.StudyGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyGroupsRepository extends JpaRepository<StudyGroups, Integer> {
    @Query("SELECT s FROM StudyGroups s WHERE s.member.memberId = :memberId")
    List<StudyGroups> findStudyGroupsByMemberId(String memberId);
}
