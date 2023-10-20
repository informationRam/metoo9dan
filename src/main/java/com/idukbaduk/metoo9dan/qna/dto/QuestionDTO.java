package com.idukbaduk.metoo9dan.qna.dto;

import com.idukbaduk.metoo9dan.common.entity.Member;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

//문의사항 게시글의 데이터 전달을 담당하는 클래스
@Data
public class QuestionDTO {
    private Integer questionNo; //문의 번호
    private String questionTitle; //문의 제목
    private String questionContent; //문의 내용
    private LocalDateTime writeDate; //작성일
    private Member member; //질문 작성자
    private Boolean isAnswered; //답변여부 했으면1, 안했으면0
}
