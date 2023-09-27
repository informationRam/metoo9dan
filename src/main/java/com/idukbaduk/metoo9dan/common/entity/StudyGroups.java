package com.idukbaduk.metoo9dan.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

//학습 그룹 테이블
@Entity
@Getter
@Setter
@Table(name="Study_groups")
public class StudyGroups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="group_no")
    private Integer groupNo; //그룹 번호

    @Column(name="group_name")
    private String groupName; //그룹명

    @Column(name="group_size")
    private Integer groupSize; //그룹인원

    @Column(name="group_start_date")
    private Date groupStartDate; //그룹 시작일

    @Column(name="group_finish_date")
    private Date groupFinishDate; //그룹 종료일

    @Column(name="group_introduce")
    private String groupIntroduce; //그룹 소개

    @ManyToOne(fetch = FetchType.LAZY) // 게임콘텐츠-다대일 관계
    @JoinColumn(name = "game_content_no", referencedColumnName = "game_content_no") // 외래 키 설정 //게임 콘텐츠 번호
    private GameContents gameContents;

    @ManyToOne(fetch = FetchType.LAZY) // 회원 - 다대일 관계
    @JoinColumn(name = "member_no", referencedColumnName = "member_no") // 외래 키 설정 //회원번호_교육자
    private Member member;


}