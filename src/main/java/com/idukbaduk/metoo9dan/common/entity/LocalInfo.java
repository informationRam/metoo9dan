package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Data;


//지역정보 테이블 - PRIMARY KEY (local_no)
@Entity
@Data
@Table(name="local_info")
public class LocalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="local_no")
    private Integer localNo;  //int            NOT NULL    AUTO_INCREMENT COMMENT '지역 번호',

    @Column
    private String sido;      //varchar(20)    NOT NULL    COMMENT '시도',

    @Column
    private String sigungu;   //varchar(20)    NOT NULL    COMMENT '시군구',
}
