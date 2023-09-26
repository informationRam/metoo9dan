package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.Entity;

import javax.management.relation.Role;
import java.time.LocalDateTime;

@Entity
public class Member {
    private Integer member_no;    //int  NOT NULL    AUTO_INCREMENT COMMENT '회원번호',
    private String name;    //varchar(50) NOT NULL    COMMENT '회원명',
    private String tel;     //varchar(20) NOT NULL    COMMENT '핸드폰번호',
    private String email;   // varchar(100) NOT NULL  COMMENT '이메일',
    private String member_id; // varchar(50) NOT NULL    COMMENT '아이디',
    private String password;  //varchar(255)  NOT NULL    COMMENT '비밀번호',
    private LocalDateTime join_date;  //datetime NOT NULL    COMMENT '가입일',
    private Role role; //enum('admin', 'educator', 'student', 'pendingapproval')    NOT NULL    COMMENT '회원구분',
    private Gender gender;           //enum('male', 'female')  NOT NULL    COMMENT '성별',
    private Boolean privacy_consent;  //boolean NOT NULL    COMMENT '개인정보동의',
    private Boolean email_consent;    //boolean NOT NULL    COMMENT '이메일수신동의',
    private Boolean sms_consent;      //boolean NOT NULL    COMMENT 'sms수신동의',
    // PRIMARY KEY (member_no, tel)

}
