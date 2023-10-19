package com.idukbaduk.metoo9dan.qna.repository;

import com.idukbaduk.metoo9dan.common.entity.QnaQuestions;
import com.idukbaduk.metoo9dan.common.entity.QuestionFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionFilesRepository extends JpaRepository<QuestionFiles, Integer> {
    //문의사항 번호로 파일 목록을 조회
    List<QuestionFiles> findByQnaQuestions(QnaQuestions questions);
}
