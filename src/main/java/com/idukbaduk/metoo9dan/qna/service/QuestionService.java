package com.idukbaduk.metoo9dan.qna.service;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.QnaQuestions;
import com.idukbaduk.metoo9dan.notice.controller.NoticeController;
import com.idukbaduk.metoo9dan.notice.exception.DataNotFoundException;
import com.idukbaduk.metoo9dan.qna.dto.QuestionDTO;
import com.idukbaduk.metoo9dan.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {
    //looger
    Logger logger = LoggerFactory.getLogger(NoticeController.class);

    private final QuestionRepository questionRepository;

    //목록조회
    public Page<QnaQuestions> getList(int pageNo) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("questionNo"));
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(sorts));
        return questionRepository.findAll(pageable);
    }

    //상세조회
    public QnaQuestions getQuestion(Integer questionNo) {
        Optional<QnaQuestions> question = questionRepository.findById(questionNo);
        if(question.isPresent()){
            return question.get();
        }else{
            throw new DataNotFoundException("QUESTION NOT FOUND");
        }
    }

    //검색 목록조회
    public Page<QnaQuestions> search(int pageNo, String searchCategory, String keyword) {
        logger.info("QuestionService의 search()진입");
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("questionNo"));
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(sorts));
        switch (searchCategory){
            case "titleAndContent":
                logger.info("case: 제목+내용");
                Page<QnaQuestions> searchList = questionRepository.findByQuestionTitleContainingOrQuestionContentContaining(keyword, keyword, pageable);
                logger.info("searchList: "+searchList);
                return searchList;
            case "title":
                logger.info("case: 제목");
                Page<QnaQuestions> searchTitleList = questionRepository.findByQuestionTitleContaining(keyword, pageable);
                logger.info("searchTitleList: "+searchTitleList);
                return searchTitleList;
            case "content":
                logger.info("case: 내용");
                Page<QnaQuestions> searchContentList = questionRepository.findByQuestionContentContaining(keyword, pageable);
                logger.info("searchContentList: "+searchContentList);
                return searchContentList;
        }
        return null;
    }

    //질문 등록처리
    public QnaQuestions add(QuestionDTO questionDTO) {
        QnaQuestions questions = new QnaQuestions();
        questions.setQuestionTitle(questionDTO.getQuestionTitle());
        questions.setQuestionContent(questionDTO.getQuestionContent());
        questions.setWriteDate(questionDTO.getWriteDate());
        questions.setMember(questionDTO.getMember());
        questions.setIsAnswered(questionDTO.getIsAnswered());
        return questionRepository.save(questions);
    }

    //질문 수정처리
    public void modify(QnaQuestions questions, QuestionDTO questionDTO) {
        questions.setQuestionTitle(questionDTO.getQuestionTitle());
        questions.setQuestionContent(questionDTO.getQuestionContent());
        questionRepository.save(questions);
    }

    //질문 삭제처리
    public void delete(QnaQuestions questions) {
        questionRepository.delete(questions);
    }


}
