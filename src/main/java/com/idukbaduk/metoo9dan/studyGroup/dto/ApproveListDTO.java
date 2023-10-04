package com.idukbaduk.metoo9dan.studyGroup.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
public class ApproveListDTO {
    private int group_students_no; //그룹 신청 번호
    private String group_name; //학습 그룹명
    private String name; //학생 이름
    private String tel; //학생 번호
    private Date application_date; //신청일
    private Boolean is_approved; //승인여부
    private Date approved_date; //승인 일자
}
