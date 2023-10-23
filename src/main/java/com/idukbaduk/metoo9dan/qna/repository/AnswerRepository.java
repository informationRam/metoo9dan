package com.idukbaduk.metoo9dan.qna.repository;

import com.idukbaduk.metoo9dan.common.entity.QnaAnswers;
import com.idukbaduk.metoo9dan.common.entity.QnaQuestions;
import com.idukbaduk.metoo9dan.qna.dto.AnswerDTOforBatis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<QnaAnswers, Integer>{
    //답변 조회
    //List<QnaAnswers> findByQnaQuestions(QnaQuestions questionNo);

    //질문번호로 답변(목록)조회
    List<QnaAnswers> findByQnaQuestions(QnaQuestions questions);

}
