package com.idukbaduk.metoo9dan.homework.validation;

import com.idukbaduk.metoo9dan.common.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Valid
@Data
public class HwSubmitForm {

    private Integer homeworkSubmitNo;    //int  NOT NULL    AUTO_INCREMENT COMMENT '숙제 제출 번호',

    @NotBlank(message = "숙제 내용을 입력해주세요")
    private String homeworkContent;      //text NOT NULL    COMMENT '숙제 내용',

    private String additionalQuestions;  //text  NULL COMMENT '추가 질의',

    private Integer homeworkNo;

    private Member member;

    private Integer sendNo;
}
