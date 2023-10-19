package com.idukbaduk.metoo9dan.qna.service;

import com.idukbaduk.metoo9dan.common.entity.QnaAnswers;
import com.idukbaduk.metoo9dan.common.entity.QnaQuestions;
import com.idukbaduk.metoo9dan.notice.controller.NoticeController;
import com.idukbaduk.metoo9dan.notice.exception.DataNotFoundException;
import com.idukbaduk.metoo9dan.qna.dto.AnswerDTO;
import com.idukbaduk.metoo9dan.qna.dto.AnswerDTOforBatis;
import com.idukbaduk.metoo9dan.qna.repository.AnswerRepository;
import com.idukbaduk.metoo9dan.qna.repository.QnaMapper;
import com.idukbaduk.metoo9dan.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {
    Logger logger = LoggerFactory.getLogger(NoticeController.class);

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final QnaMapper mapper;

    //답변등록처리
    public void add(AnswerDTO answerDTO) {
        QnaAnswers answers = new QnaAnswers();
        answers.setAnswerTitle(answerDTO.getAnswerTitle());
        answers.setAnswerContent(answerDTO.getAnswerContent());
        answers.setWriteDate(answerDTO.getWriteDate());
        answers.setMember(answerDTO.getMember());
        answers.setQnaQuestions(answerDTO.getQnaQuestions());
        answerRepository.save(answers);
    }

    //해당 질문의 답변여부 컬럼 업데이트
    public void updateIsAnswered(QnaQuestions questions) {
        questions.setIsAnswered(true);
        questionRepository.save(questions);
    }

    /*//답변 조회 1트
    public QnaAnswers getAnswer(QnaQuestions questions) {
        Optional<QnaAnswers> answers = answerRepository.findByQnaQuestions(questions);
        if(answers.isPresent()) {
            return answers.get();
        }
        return null;
    }*/
    /*/답변 조회 2트
    public List<QnaAnswers> getAnswer(QnaQuestions questions) {
        return answerRepository.findByQnaQuestions(questions);
    }*/
    @Autowired
    private SqlSession sqlSession;

    /*/답변 조회 3트
    public AnswerDTOforBatis getAnswer(Integer questionNo){
        logger.info("questionNo: "+questionNo);
        logger.info("sqlSession: "+sqlSession);
        logger.warn("myBatis: "+sqlSession.selectOne("qna.getAnswer", questionNo));
        return sqlSession.selectOne("qna.getAnswer", questionNo);
    }*/

    /*/답변조회 4트
    public List<AnswerDTOforBatis> getAnswersByQuestionNo(int questionNo) {
        List<AnswerDTOforBatis> list = mapper.getAnswersByQuestionNo(questionNo);
        logger.info("getAnswer:"+ list);
        return list;
    }*/

    /*//5트
    public List<AnswerDTOforBatis> getAnswerByQuestionNo(int questionNo){
        List<AnswerDTOforBatis> list = sqlSession.selectOne("qna.getAnswersByQuestionNo", questionNo);
        logger.info("list: "+list);
        return list;
    }*/

    //6트
    public List<HashMap<String, Object>> getAnswerByQuestionNo(){
        return sqlSession.selectList("qna.getAnswersByQuestionNo");
    }

    //7트 _엔티티수정
    //답변 목록조회
    public List<QnaAnswers> getAnswers(QnaQuestions questions){
        List<QnaAnswers> answers = answerRepository.findByQnaQuestions(questions);
        return answers;
    }

    //8트
    public QnaAnswers getAnswers(QnaQuestions questions){
        return mapper.getAnswers(questions.getQuestionNo());
    }
}
