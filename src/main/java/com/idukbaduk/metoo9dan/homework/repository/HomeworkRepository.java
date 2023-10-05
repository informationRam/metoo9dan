package com.idukbaduk.metoo9dan.homework.repository;

import com.idukbaduk.metoo9dan.common.entity.Homeworks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homeworks, Integer> {
    //Page<Homeworks> findByMember_MemberIdOrderByCreationDateDesc(String memberId, Pageable pageable);
    List<Homeworks> findByMember_MemberIdOrderByCreationDateDesc(String memberId);
    @Query("SELECT h FROM Homeworks h WHERE h.member.memberId = :memberId AND h.dueDate > :currentDate ORDER BY h.creationDate DESC")
    List<Homeworks> findHomeworksByMemberIdAndDueDateAfter(String memberId, Date currentDate);
}

