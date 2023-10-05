package com.idukbaduk.metoo9dan.homework.repository;

import com.idukbaduk.metoo9dan.common.entity.HomeworkSend;
import com.idukbaduk.metoo9dan.common.entity.Homeworks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeworkSendRepository extends JpaRepository<HomeworkSend, Integer> {
    Optional<Object> findByHomeworks(Homeworks hw);
    List<HomeworkSend> findByHomeworks_HomeworkNo(Integer homeworkNo);
    @Query("SELECT h FROM HomeworkSend h WHERE h.member.memberNo = :memberNo ORDER BY h.sendDate DESC")
    List<HomeworkSend> findLatestHomeworkSendByMemberNo(Integer memberNo);
}
