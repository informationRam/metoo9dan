package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="QNA_ANSWERS")
public class QnaAnswers {
    //문의 답변 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="answer_no")
    private Integer answerNo; //답변 번호

    @OneToOne(fetch = FetchType.LAZY) //일대일
    @JoinColumn(name="question_no") //질문 번호
    private QnaQuestions qnaQuestions;

    @Column(name="answer_title") //답변 제목
    private String answerTitle;

    @Column(name="answer_content") //답변 내용
    private String answerContent;

    @Column(name="write_date") //작성일
    private LocalDateTime writeDate;

    @ManyToOne(fetch = FetchType.LAZY) //다대일
    @JoinColumn(name="member_no", referencedColumnName = "member_no")
    private Member member; //답변 작성자


}
