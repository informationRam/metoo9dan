package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="QNA_QUESTIONS")
public class QnaQuestions {
    //문의 질문 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="question_no")
    private Integer questionNo; //문의 번호

    @Column(name="question_title") //문의 제목
    private String questionTitle;

    @Column(name="question_content") //문의 내용
    private String questionContent;

    @Column(name="write_date") //작성일
    private LocalDateTime writeDate;

    @ManyToOne(fetch = FetchType.LAZY) //다대일
    @JoinColumn(name="member_no", referencedColumnName = "member_no")
    private Member member; //질문 작성자

    @Column(name="is_answered") //답변여부
    private Boolean isAnswered;

    //이 해당필드가 QnaAnswers 엔티티의 'qnaAnswers 필드'에 의해 매핑된다는 의미.
    @OneToOne(mappedBy = "qnaQuestions", cascade = CascadeType.ALL)
    private QnaAnswers qnaAnswers;

    //이 해당필드가 QuestionFiles 엔티티의 'qnaQuestions 필드'에 의해 매핑된다는 의미.
    @OneToMany(mappedBy = "qnaQuestions", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionFiles> questionFiles;

}
