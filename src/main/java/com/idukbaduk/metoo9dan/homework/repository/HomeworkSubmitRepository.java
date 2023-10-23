package com.idukbaduk.metoo9dan.homework.repository;

import com.idukbaduk.metoo9dan.common.entity.HomeworkSend;
import com.idukbaduk.metoo9dan.common.entity.HomeworkSubmit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeworkSubmitRepository extends JpaRepository<HomeworkSubmit, Integer> {
    Optional<HomeworkSubmit> findByHomeworkSend_SendNo(Integer sendNo);

    Optional<HomeworkSubmit> findByHomeworkSend(HomeworkSend hs);

    List<HomeworkSubmit> findByMember_MemberId(String name);
}
