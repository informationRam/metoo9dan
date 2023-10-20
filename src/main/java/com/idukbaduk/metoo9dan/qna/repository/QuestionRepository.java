package com.idukbaduk.metoo9dan.qna.repository;

import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.QnaQuestions;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    //내가 작성한 문의사항만 조회
    Page<QnaQuestions> findByMember(Member member, Pageable pageable);

    //내가 작성한 문의사항만 검색
    //제목+내용
    Page<QnaQuestions> findByMemberAndQuestionTitleContainingOrQuestionContentContaining(Member member, String keyword, String keyword1, Pageable pageable);
    //제목
    Page<QnaQuestions> findByMemberAndQuestionTitleContaining(Member member, String keyword, Pageable pageable);
    //내용
    Page<QnaQuestions> findByMemberAndQuestionContentContaining(Member member, String keyword, Pageable pageable);


}
