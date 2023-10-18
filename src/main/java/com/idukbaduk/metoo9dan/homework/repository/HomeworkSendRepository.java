package com.idukbaduk.metoo9dan.homework.repository;

import com.idukbaduk.metoo9dan.common.entity.HomeworkSend;
import com.idukbaduk.metoo9dan.common.entity.Homeworks;
import com.idukbaduk.metoo9dan.common.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface HomeworkSendRepository extends JpaRepository<HomeworkSend, Integer> {
    List<HomeworkSend> findByHomeworks(Homeworks hw);
    List<HomeworkSend> findByHomeworks_HomeworkNo(Integer homeworkNo);
    @Query("SELECT h FROM HomeworkSend h WHERE h.member.memberNo = :memberNo ORDER BY h.sendDate DESC")
    List<HomeworkSend> findLatestHomeworkSendByMemberNo(Integer memberNo);

    boolean existsByHomeworksAndMember(Homeworks homework, Member member);

    @Query("SELECT hs FROM HomeworkSend hs WHERE hs.member.memberId = :memberId AND hs.homeworks.dueDate >= CURDATE()")
    List<HomeworkSend> findByMemberIdAndDueDateAfterCurrentDate(String memberId);

    @Query("SELECT hs FROM HomeworkSend hs WHERE hs.member.memberId = :memberId AND hs.homeworks.dueDate < CURDATE()")
    List<HomeworkSend> findByMemberIdAndDueDateBeforeCurrentDate(String memberId);

    Page<HomeworkSend> findByHomeworks_HomeworkTitleContaining(String title, Pageable pageable);

    @Query("SELECT hs.homeworks FROM HomeworkSend hs WHERE hs.homeworks.member.memberId = :memberId")
    List<Homeworks> findHomeworksByMemberId(String memberId);

    List<HomeworkSend> findByHomeworks_HomeworkNoAndSendDate(int homeworkNo, LocalDateTime sendDate);

    @Query("SELECT hs FROM HomeworkSend hs WHERE hs.homeworks.member.memberId = :memberId")
    Page<HomeworkSend> findAllByMemberId(String memberId, Pageable pageable);

    @Query("SELECT hs FROM HomeworkSend hs JOIN hs.homeworks h JOIN h.member m WHERE m.memberId = :memberId AND h.homeworkTitle = :homeworkTitle")
    Page<HomeworkSend> findByMemberIdAndTitle(String memberId, String homeworkTitle, Pageable pageable);
}
