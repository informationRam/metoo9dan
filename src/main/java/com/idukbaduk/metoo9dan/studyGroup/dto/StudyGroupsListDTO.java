package com.idukbaduk.metoo9dan.studyGroup.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Getter
@Setter
@ToString
public class StudyGroupsListDTO {
    private int group_no;
    private String group_name;//학습그룹명
    private String game_name;//게임콘텐츠명
    private int group_TO;//그룹to
    private Date group_start_date; //그룹 시작일
    private Date group_finish_date; //그룹 종료일
    //private int subscription_duration; //구독 기간
    private int aproved_num; //등록학생수

}
