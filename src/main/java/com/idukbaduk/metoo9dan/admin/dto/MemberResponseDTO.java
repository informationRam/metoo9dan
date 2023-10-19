package com.idukbaduk.metoo9dan.admin.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@Data
public class MemberResponseDTO {
        private Integer memberNo;       //int  NOT NULL    AUTO_INCREMENT COMMENT '회원번호',
        private String name;            //varchar(50) NOT NULL    COMMENT '회원명',
        private String tel;             //varchar(20) NOT NULL    COMMENT '핸드폰번호',
        private String email;           // varchar(100) NOT NULL  COMMENT '이메일',
        private String memberId;        // varchar(50) NOT NULL    COMMENT '아이디',
        private String password;        //varchar(255)  NOT NULL    COMMENT '비밀번호',
        //private LocalDateTime joinDate;  //datetime NOT NULL    COMMENT '가입일',
        private String role;            //enum('admin', 'educator', 'student', 'pendingapproval')    NOT NULL    COMMENT '회원구분',
        private String gender;           //enum('male', 'female')  NOT NULL    COMMENT '성별',
        private Boolean privacyConsent;  //boolean NOT NULL    COMMENT '개인정보동의',
        private Boolean emailConsent;    //boolean NOT NULL    COMMENT '이메일수신동의',
        private Boolean smsConsent;      //boolean NOT NULL    COMMENT 'sms수신동의',
        private String memberMemo;
        private Date birth;
        private String membershipStatus;

    }
