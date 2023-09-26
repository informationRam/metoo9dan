package com.idukbaduk.metoo9dan.common.entity;


import jakarta.persistence.*;
import lombok.Data;

//교육자 정보 테이블 -PRIMARY KEY (member_no)
@Entity
@Data
@Table(name="educator_info")
public class EducatorInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_no")
    private Integer memberNo;    //int NOT NULL    COMMENT '회원번호',

    @Column
    private String sido;         //varchar(20)    NOT NULL    COMMENT '시도',

    @Column
    private String sigungu;      //varchar(20)    NOT NULL    COMMENT '시군구',

    @Column(name="school_name")
    private String schoolName;  //varchar(20)    NOT NULL    COMMENT '소속명',

}
