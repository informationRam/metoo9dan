package com.idukbaduk.metoo9dan.qna.repository;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.QnaQuestions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<QnaQuestions, Integer> {
    //검색
    //제목+내용
    Page<QnaQuestions> findByQuestionTitleContainingOrQuestionContentContaining(String keyword, String keyword1, Pageable pageable);
    //제목
    Page<QnaQuestions> findByQuestionTitleContaining(String keyword, Pageable pageable);
    //내용
    Page<QnaQuestions> findByQuestionContentContaining(String keyword, Pageable pageable);
}
