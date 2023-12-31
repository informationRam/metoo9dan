package com.idukbaduk.metoo9dan.homework.repository;

import com.idukbaduk.metoo9dan.common.entity.HomeworkSend;
import com.idukbaduk.metoo9dan.common.entity.Homeworks;
import com.idukbaduk.metoo9dan.common.entity.Member;
import org.springframework.data.repository.query.Param;
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
    List<HomeworkSend> findLatestHomeworkSendByMemberNo( @Param("memberNo") Integer memberNo);

    boolean existsByHomeworksAndMember(Homeworks homework, Member member);

    @Query("SELECT hs FROM HomeworkSend hs WHERE hs.member.memberId = :memberId AND hs.homeworks.dueDate >= CURDATE()")
    List<HomeworkSend> findByMemberIdAndDueDateAfterCurrentDate(@Param("memberId") String memberId);

    @Query("SELECT hs FROM HomeworkSend hs WHERE hs.member.memberId = :memberId AND hs.homeworks.dueDate < CURDATE()")
    List<HomeworkSend> findByMemberIdAndDueDateBeforeCurrentDate(@Param("memberId") String memberId);

    Page<HomeworkSend> findByHomeworks_HomeworkTitleContaining(String title, Pageable pageable);

    @Query("SELECT hs.homeworks FROM HomeworkSend hs WHERE hs.homeworks.member.memberId = :memberId")
    List<Homeworks> findHomeworksByMemberId( @Param("memberId") String memberId);

    List<HomeworkSend> findByHomeworks_HomeworkNoAndSendDate(int homeworkNo, LocalDateTime sendDate);

    @Query("SELECT hs FROM HomeworkSend hs " +
            "WHERE hs.homeworks.member.memberId = :memberId " +
            "GROUP BY hs.sendDate, hs.homeworks.homeworkTitle")
    Page<HomeworkSend> findAllByMemberId(@Param("memberId") String memberId, Pageable pageable);

    @Query("SELECT hs FROM HomeworkSend hs JOIN hs.homeworks h JOIN h.member m " +
            "WHERE m.memberId = :memberId AND h.homeworkTitle = :homeworkTitle " +
            "GROUP BY hs.sendDate, h.homeworkTitle")
    Page<HomeworkSend> findByMemberIdAndTitle(@Param("memberId") String memberId, @Param("homeworkTitle") String homeworkTitle, Pageable pageable);

    List<HomeworkSend> findByHomeworks_GameTitleAndMember_MemberIdAndHomeworks_DueDateBefore(String gameTitle, String memberId, LocalDateTime currentDate);
}
