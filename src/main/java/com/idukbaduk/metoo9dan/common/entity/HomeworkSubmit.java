package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// 숙제제출 테이블 - PRIMARY KEY (homework_submit_no)
@Entity
@Data
@Table(name="homework_submit")
public class HomeworkSubmit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="homework_submit_no")
    private Integer homeworkSubmitNo;    //int  NOT NULL    AUTO_INCREMENT COMMENT '숙제 제출 번호',

    @Column
    private String progress;              //varchar(50) NOT NULL    COMMENT '진도',

    @Column(name="homework_content")
    private String homeworkContent;      //text NOT NULL    COMMENT '숙제 내용',

    @Column(name="additional_questions")
    private String additionalQuestions;  //text  NULL COMMENT '추가 질의',

    @Column(name="submit_date",columnDefinition = "TIMESTAMP")
    private LocalDateTime submitDate;    //datetime  NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '제출일',

    @Column
    private String evaluation;            //text NULL COMMENT '평가',

    @ManyToOne(fetch = FetchType.LAZY) // 숙제-다대일 관계
    @JoinColumn(name = "homework_no", referencedColumnName = "homework_no") // 외래 키 설정
    private Homeworks homeworks;

    @ManyToOne(fetch = FetchType.LAZY) // 회원-다대일 관계
    @JoinColumn(name = "member_no", referencedColumnName = "member_no") // 외래 키 설정
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId //@MapsId 는 @id로 지정한 컬럼에 @OneToOne 이나 @ManyToOne 관계를 매핑시키는 역할
    @JoinColumn(name = "send_no", referencedColumnName = "send_no") // 외래 키 설정
    private HomeworkSend homeworkSend;
}
