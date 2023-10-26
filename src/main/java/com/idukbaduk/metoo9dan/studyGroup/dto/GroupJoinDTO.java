package com.idukbaduk.metoo9dan.studyGroup.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
public class GroupJoinDTO {
    private LocalDateTime application_date; //신청일
    private Boolean is_approved; //승인여부
    private LocalDateTime approved_date; //승인 일자

}
