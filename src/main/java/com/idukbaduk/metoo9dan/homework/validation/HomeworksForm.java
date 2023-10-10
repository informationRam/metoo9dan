package com.idukbaduk.metoo9dan.homework.validation;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Valid
@Data
public class HomeworksForm {
    @Column(name="homework_no")
    private Integer homeworkNo;

    @Column(name="homework_title")
    @NotBlank(message = "숙제 제목을 입력해주세요")
    @Size(max=100, message = "숙제 제목은 100자 이내로 입력 가능합니다")
    private String homeworkTitle;

    @Column(name="homework_content")
    @NotBlank(message = "숙제 내용을 입력해주세요")
    private String homeworkContent;

    @Column(name="homework_memo")
    private String homeworkMemo;

    @Column
    @NotNull(message = "진도를 입력해주세요")
    private Integer progress;

    @Column(name="due_date")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Future(message = "제출기한은 지나간 날짜로 설정하실 수 없습니다")
    @NotNull(message = "제출기한을 입력해주세요")
    private Date dueDate;
}
