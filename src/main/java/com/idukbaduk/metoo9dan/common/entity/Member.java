package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.management.relation.Role;
import java.time.LocalDateTime;

// 회원테이블 - PRIMARY KEY (member_no, tel)

@Entity
@Data
@Table(name="members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_no")
    private Integer memberNo;       //int  NOT NULL    AUTO_INCREMENT COMMENT '회원번호',

    @Column
    private String name;            //varchar(50) NOT NULL    COMMENT '회원명',

    @Column(unique = true)
    private String tel;             //varchar(20) NOT NULL    COMMENT '핸드폰번호',

    @Column
    private String email;           // varchar(100) NOT NULL  COMMENT '이메일',

    @Column(name="member_id")
    private String memberId;        // varchar(50) NOT NULL    COMMENT '아이디',

    @Column
    private String password;        //varchar(255)  NOT NULL    COMMENT '비밀번호',

    @Column(name="join_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime joinDate;  //datetime NOT NULL    COMMENT '가입일',

    @Column
    private String role;            //enum('admin', 'educator', 'student', 'pendingapproval')    NOT NULL    COMMENT '회원구분',

    @Column
    private String gender;           //enum('male', 'female')  NOT NULL    COMMENT '성별',

    @Column(name="privacy_consent",columnDefinition = "TINYINT(1)")
    private Boolean privacyConsent;  //boolean NOT NULL    COMMENT '개인정보동의',

    @Column(name="email_consent",columnDefinition = "TINYINT(1)")
    private Boolean emailConsent;    //boolean NOT NULL    COMMENT '이메일수신동의',

    @Column(name="sms_consent",columnDefinition = "TINYINT(1)")
    private Boolean smsConsent;      //boolean NOT NULL    COMMENT 'sms수신동의',

}
