package com.idukbaduk.metoo9dan.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="Study_groups")
public class StudyGroups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="group_no")
    private Integer groupNo;

    @Column(name="group_name")
    private String groupName;

    @Column(name="group_size")
    private Integer groupSize;

    @Column(name="group_start_date")
    private Date groupStartDate;

    @Column(name="group_finish_date")
    private Date groupFinishDate;

    @Column(name="group_introduce")
    private String groupIntroduce;

    @ManyToOne(fetch = FetchType.LAZY) // 게임콘텐츠-다대일 관계
    @JoinColumn(name = "game_content_no", referencedColumnName = "game_content_no") // 외래 키 설정
    private GameContents gameContents;

    @ManyToOne(fetch = FetchType.LAZY) // 회원 - 다대일 관계
    @JoinColumn(name = "member_no", referencedColumnName = "member_no") // 외래 키 설정
    private Member member;


}