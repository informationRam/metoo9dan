package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

//결제 테이블 - PRIMARY KEY (payment_no)
@Entity
@Data
@Table(name="payments")
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="payment_no")
    private Integer paymentNo;       //int  NOT NULL    AUTO_INCREMENT COMMENT '결제 번호',

    @Column(name="order_number")
    private String orderNumber;     //varchar(50) NOT NULL    COMMENT '주문 번호',

    @Column
    private String contact;          //varchar(20)  NOT NULL    COMMENT '연락처',

    @Column
    private String method;           //enum('directtransfer', 'bankaccount', 'kakaopay')    NOT NULL    COMMENT '결제 수단',

    @Column(name="payment_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime paymentDate;     //datetime  NOT NULL    COMMENT '결제일',

    @Column
    private String status;           //enum('processing', 'completed') NOT NULL    COMMENT '결제 상태',

    @Column
    private Double amount;           //decimal(10,2) NOT NULL    COMMENT '결제 금액',

    @Column(name="depositor_name")
    private String depositorName;   //varchar(50)  NULL COMMENT '입금자명',

    @ManyToOne(fetch = FetchType.LAZY) // 게임콘텐츠-다대일 관계
    @JoinColumn(name = "game_content_no", referencedColumnName = "game_content_no") // 외래 키 설정
    private GameContents gameContents;

    @ManyToOne(fetch = FetchType.LAZY) // 회원-다대일 관계
    @JoinColumn(name = "member_no", referencedColumnName = "member_no") // 외래 키 설정
    private Member member;

}
