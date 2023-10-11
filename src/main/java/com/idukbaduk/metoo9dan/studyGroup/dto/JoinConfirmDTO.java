package com.idukbaduk.metoo9dan.studyGroup.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
public class JoinConfirmDTO {
    private int group_students_no; //그룹 신청 번호
    private String game_name;//게임콘텐츠명
    private String group_name;//학습그룹명
    private String name;//교육자명
    private Date group_start_date; //그룹 시작일
    private Date group_finish_date; //그룹 종료일
    private Date application_date; //가입 요청일
    private Date approved_date; //가입 승인일
    private Boolean is_approved; //승인여부

}
