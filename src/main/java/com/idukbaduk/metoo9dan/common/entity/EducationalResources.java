package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

//교육자료테이블 - PRIMARY KEY (resource_no)
@Entity
@Data
@Table(name="educational_resources")
public class EducationalResources {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="resource_no")
    private Integer resourceNo;      //int  NOT NULL    AUTO_INCREMENT COMMENT '교육자료 번호'

    @Column(name="resource_name")
    private String resourceName;    //varchar(100) NOT NULL    COMMENT '교육자료명',

    @Column(name="resource_cate")
    private String resourceCate;    //varchar(50) NOT NULL    COMMENT '자료 구분',

    @Column(name="file_type")
    private String fileType;        //varchar(50) NOT NULL    COMMENT '자료 유형',

    @Column(name="file_url")
    private String fileUrl;         //text  NOT NULL    COMMENT '자료url',

    @Column(name="service_type")
    private String serviceType;     //enum('paid', 'free')    NOT NULL    COMMENT '서비스 구분',

    @Column
    private String description;      //text NOT NULL    COMMENT '자료내용',

    @Column(name="creation_date",columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDate;    //datetime  NOT NULL    COMMENT '등록일',

    @Column(name="game_content_no")
    private Integer gameContentNo;  //int NOT NULL    COMMENT '게임 컨텐츠 번호',

    @ManyToOne(fetch = FetchType.LAZY) // 게임콘텐츠-다대일 관계
    @JoinColumn(name = "game_content_no", referencedColumnName = "game_content_no", insertable = false, updatable = false) // 외래 키 설정
    private GameContents gameContents;
}
