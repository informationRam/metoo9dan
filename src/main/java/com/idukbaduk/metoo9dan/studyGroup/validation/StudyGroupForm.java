package com.idukbaduk.metoo9dan.studyGroup.validation;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StudyGroupForm {

    @NotEmpty(message = "그룹명은 필수 입력 입니다.")
    private String groupName; //그룹명

    @NotEmpty(message = "그룹인원은 필수 입력 입니다.")
    private Integer groupSize; //그룹인원

    @NotEmpty(message = "그룹 시작일은 필수 입력 입니다.")
    private Date groupStartDate; //그룹 시작일

    @NotEmpty(message = "그룹 종료일은 필수 입력 입니다.")
    private Date groupFinishDate; //그룹 종료일

    private String groupIntroduce; //그룹 소개

}
