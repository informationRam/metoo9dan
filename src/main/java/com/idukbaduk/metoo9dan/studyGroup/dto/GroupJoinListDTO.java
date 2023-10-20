package com.idukbaduk.metoo9dan.studyGroup.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class GroupJoinListDTO {
    private int group_no;
    private String copy_file_name; //콘텐츠 이미지 가져와야함.
    private String group_name;//학습그룹명
    private String name; //교육자명
    private Date group_start_date; //그룹 시작일(학습구독기간)
    private Date group_finish_date; //그룹 종료일(학습구독기간)
    private int group_TO;//그룹to

}
