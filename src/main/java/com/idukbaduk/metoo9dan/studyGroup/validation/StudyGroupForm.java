package com.idukbaduk.metoo9dan.studyGroup.validation;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.util.Date;

@Getter
@Setter
@Validated
public class StudyGroupForm {

    @NotEmpty(message = "그룹명은 필수 입력 입니다.")
    private String groupName; //그룹명

    @NotNull(message = "그룹인원은 필수 입력 입니다.")
    private Integer groupSize; //그룹인원

    @NotNull(message = "그룹 시작일은 필수 입력 입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date groupStartDate; //그룹 시작일

    @NotNull(message = "그룹 종료일은 필수 입력 입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date groupFinishDate; //그룹 종료일

    @Valid
    private String groupIntroduce; //그룹 소개

    @NotNull
    @Min(value = 1)
    private Integer gameContentNo;

    @NotNull
    @Min(value = 1)
    private Integer memberNo;

}
