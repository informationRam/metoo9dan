package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// 숙제전송테이블
@Entity
@Data
@Table(name="homework_send")
public class HomeworkSend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="send_no")
    private Integer sendNo;        //int  NOT NULL    AUTO_INCREMENT COMMENT '전송번호',

    @Column(name="current_level")
    private Integer currentLevel;  //varchar(50) NOT NULL    COMMENT '현재 레벨',

    @Column(name="send_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime sendDate; //datetime       NOT NULL    COMMENT '전송일',

    @ManyToOne(fetch = FetchType.LAZY) // 숙제-다대일 관계
    @JoinColumn(name = "homework_no", referencedColumnName = "homework_no") // 외래 키 설정
    private Homeworks homeworks;

    @ManyToOne(fetch = FetchType.LAZY) // 회원-다대일 관계
    @JoinColumn(name = "member_no", referencedColumnName = "member_no") // 외래 키 설정
    private Member member;

    @Column(name="is_submit")
    private String isSubmit;
}
