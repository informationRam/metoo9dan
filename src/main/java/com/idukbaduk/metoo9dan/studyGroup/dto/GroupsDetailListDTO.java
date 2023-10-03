package com.idukbaduk.metoo9dan.studyGroup.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class GroupsDetailListDTO {
    private int group_no;
    private String group_name;//학습그룹명
    private String game_name;//게임콘텐츠명
    private int group_size;//그룹 제한 인원
    private int approved_num; //그룹가입인원
    private String name; //그룹가입학생
    private String tel; //그룹가입학생 번호
    private String email; //그룹가입학생 이메일
    private Date join_date; //그룹가입학생 가입일

}
