package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

//숙제 테이블 - PRIMARY KEY (homework_no)
@Entity
@Data
@Table(name="homeworks")
@ToString
public class Homeworks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="homework_no")
    private Integer homeworkNo;         //int  NOT NULL    AUTO_INCREMENT COMMENT '숙제 번호',

    @Column(name="homework_title")
    private String homeworkTitle;       //varchar(100)    NOT NULL    COMMENT '숙제 제목',

    @Column(name="homework_content")
    private String homeworkContent;     //text NOT NULL    COMMENT '숙제 내용',

    @Column(name="homework_memo")
    private String homeworkMemo;     //text NOT NULL    COMMENT '숙제 내용',

    @Column
    private Integer progress;            //varchar(50) NOT NULL    COMMENT '진도',

    @Column(name="due_date")
    private Date dueDate;               //date NOT NULL    COMMENT '제출기한',

    @Column(name="creation_date")
    private LocalDateTime creationDate;        //date  NOT NULL    COMMENT '생성일',

    @ManyToOne(fetch = FetchType.LAZY) // 회원 - 다대일 관계
    @JoinColumn(name = "member_no", referencedColumnName = "member_no") // 외래 키 설정,
    private Member member;

    @Column(name="game_content_title")
    private String gameTitle;

    @Column(name="status")
    private String status;     //text NOT NULL    COMMENT '숙제 내용'

}
