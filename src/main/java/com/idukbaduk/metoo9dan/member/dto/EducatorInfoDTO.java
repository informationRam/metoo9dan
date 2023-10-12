package com.idukbaduk.metoo9dan.member.dto;

import lombok.Getter;
import lombok.Setter;

//교육자 정보 테이블 -PRIMARY KEY (member_no)

@Getter
@Setter
public class EducatorInfoDTO {

    private Integer memberNo;
    private String sido;         //varchar(20)    NOT NULL    COMMENT '시도',
    private String sigungu;      //varchar(20)    NOT NULL    COMMENT '시군구',
    private String schoolName;  //varchar(20)    NOT NULL    COMMENT '소속명',

}
