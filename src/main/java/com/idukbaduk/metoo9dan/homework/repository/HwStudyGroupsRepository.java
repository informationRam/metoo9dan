package com.idukbaduk.metoo9dan.homework.repository;

import com.idukbaduk.metoo9dan.common.entity.StudyGroups;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HwStudyGroupsRepository extends JpaRepository<StudyGroups, Integer> {
    @Query("SELECT s FROM StudyGroups s WHERE s.member.memberId = :memberId")
    List<StudyGroups> findStudyGroupsByMemberId(@Param("memberId") String memberId);
}
