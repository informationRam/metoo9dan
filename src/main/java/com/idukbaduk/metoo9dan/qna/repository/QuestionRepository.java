package com.idukbaduk.metoo9dan.qna.repository;

import com.idukbaduk.metoo9dan.common.entity.QnaQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<QnaQuestions, Integer> {
}
