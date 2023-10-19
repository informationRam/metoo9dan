package com.idukbaduk.metoo9dan.qna.dto;

import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.QnaQuestions;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerDTO {
    private Integer answerNo; //답변 번호
    private QnaQuestions qnaQuestions; //질문번호
    private String answerTitle; //제목
    private String answerContent; //내용
    private LocalDateTime writeDate; //작성일
    private Member member; //답변 작성자

}
