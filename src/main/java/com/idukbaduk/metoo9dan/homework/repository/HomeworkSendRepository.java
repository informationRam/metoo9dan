package com.idukbaduk.metoo9dan.homework.repository;

import com.idukbaduk.metoo9dan.common.entity.HomeworkSend;
import com.idukbaduk.metoo9dan.common.entity.Homeworks;
import com.idukbaduk.metoo9dan.common.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeworkSendRepository extends JpaRepository<HomeworkSend, Integer> {
    List<HomeworkSend> findByHomeworks(Homeworks hw);
    List<HomeworkSend> findByHomeworks_HomeworkNo(Integer homeworkNo);
    @Query("SELECT h FROM HomeworkSend h WHERE h.member.memberNo = :memberNo ORDER BY h.sendDate DESC")
    List<HomeworkSend> findLatestHomeworkSendByMemberNo(Integer memberNo);

    boolean existsByHomeworksAndMember(Homeworks homework, Member member);

    @Query("SELECT hs FROM HomeworkSend hs WHERE hs.member.memberId = :memberId AND hs.homeworks.dueDate > CURDATE()")
    List<HomeworkSend> findByMemberIdAndDueDateAfterCurrentDate(String memberId);
}
