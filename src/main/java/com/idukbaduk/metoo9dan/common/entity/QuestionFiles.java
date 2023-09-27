package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="QUESTION_FILES")
public class QuestionFiles {
    //문의사항 첨부파일 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="file_no")
    private Integer fileNo; //파일 번호

    @ManyToOne(fetch = FetchType.LAZY) //다대일
    @JoinColumn(name="question_no", referencedColumnName = "question_no")
    private QnaQuestions qnaQuestions; //문의 번호

    @Column(name="origin_file_name")
    private String origin_file_name; //원본파일명

    @Column(name="copy_file_name")
    private String copy_file_name; //사본파일명
}
