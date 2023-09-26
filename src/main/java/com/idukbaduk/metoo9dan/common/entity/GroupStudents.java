package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="group_students")
public class GroupStudents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="group_students")
    private Integer groupStudents;

    @Column(name="group_no")
    private Integer groupNo;

    @Column(name="application_date")
    private LocalDateTime applicationDate;

    @Column(name="is_approved")
    private boolean isApproved;

    @Column(name="approved_date")
    private LocalDateTime approvedDate;

    @ManyToOne(fetch = FetchType.LAZY) // 학습그룹테이블-다대일 관계
    @JoinColumn(name = "game_content_no", referencedColumnName = "game_content_no") // 외래 키 설정
    private StudyGroups studyGroups;

    @ManyToOne(fetch = FetchType.LAZY) // 회원테이블-다대일 관계
    @JoinColumn(name = "member_no", referencedColumnName = "member_no") // 외래 키 설정
    private Member member;

}