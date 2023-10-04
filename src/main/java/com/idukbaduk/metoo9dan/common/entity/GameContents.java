package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;

import lombok.Data;

import java.time.LocalDateTime;

// 게임콘텐츠 - PRIMARY KEY (game_content_no)
@Entity
@Data
@Table(name="game_contents")
public class GameContents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="game_content_no")
    private Integer gameContentNo;        //int  NOT NULL    AUTO_INCREMENT COMMENT '게임 콘텐츠 번호',

    @Column(name="game_name")
    private String gameName;             //varchar(100) NOT NULL    COMMENT '게임 콘텐츠명',

    @Column
    private String difficulty;             //varchar(50) NOT NULL    COMMENT '난이도',

    @Column(name="subscription_duration")
    private Integer subscriptionDuration;  //int NOT NULL    COMMENT '구독기간',

    @Column(name="max_subscribers")
    private Integer maxSubscribers;        //int  NOT NULL    COMMENT '구독 인원',

    @Column(name="original_price")
    private Double originalPrice;         //decimal(10,2) NOT NULL    COMMENT '정가',

    @Column(name="discount_rate")
    private Double discountRate;          //decimal(5,2) NOT NULL    COMMENT '할인율',

    @Column(name="sale_price")
    private Double salePrice;             //decimal(10,2) NOT NULL    COMMENT '판매가',

    @Column(name="package_details")
    private String packageDetails;        //text NOT NULL    COMMENT '패키지 내용',

    @Column(name="creation_date",columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDate;  //datetime  NOT NULL    COMMENT '등록일',

    @Column
    private String status;                //enum('posted', 'notposted') NOT NULL    COMMENT '게시글 상태',

    @Column(name="content_type")
    private String contentType;           //enum('package','individual') NOT NULL    COMMENT '콘텐츠 타입',



}
