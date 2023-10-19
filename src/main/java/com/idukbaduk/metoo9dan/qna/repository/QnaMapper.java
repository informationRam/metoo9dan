package com.idukbaduk.metoo9dan.qna.repository;

import com.idukbaduk.metoo9dan.common.entity.QnaAnswers;
import com.idukbaduk.metoo9dan.qna.dto.AnswerDTOforBatis;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface QnaMapper {
    //답변 조회 _myBatis
    List<AnswerDTOforBatis> getAnswersByQuestionNo(int questionNo);

    QnaAnswers getAnswers(Integer questionNo);
}
