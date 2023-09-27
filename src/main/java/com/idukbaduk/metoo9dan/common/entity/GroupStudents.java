package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//그룹 학생 테이블
@Entity
@Getter
@Setter
@Table(name="group_students")
public class GroupStudents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="group_students_no")
    private Integer groupStudentsNO;  //그룹신청번호

    @Column(name="application_date")
    private LocalDateTime applicationDate;  //신청일

    @Column(name="is_approved")
    private Boolean isApproved; //승인여부

    @Column(name="approved_date")
    private LocalDateTime approvedDate; //승인 일자

    @ManyToOne(fetch = FetchType.LAZY) // 학습그룹테이블-다대일 관계
    @JoinColumn(name = "group_no", referencedColumnName = "group_no") // 외래 키 설정  //그룹번호
    private StudyGroups studyGroups;

    @ManyToOne(fetch = FetchType.LAZY) // 회원테이블-다대일 관계
    @JoinColumn(name = "member_no", referencedColumnName = "member_no") // 외래 키 설정 //회원번호_학생
    private Member member;

}