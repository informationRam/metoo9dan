package com.idukbaduk.metoo9dan.homework.validation;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Valid
@Data
public class HomeworksEditForm {
    @Column(name="homework_no")
    private Integer hwNo;

    @Column(name="homework_title")
    @NotBlank(message = "숙제 제목을 입력해주세요")
    @Size(max=100, message = "숙제 제목은 100자 이내로 입력 가능합니다")
    private String hwTitle;

    @Column(name="homework_content")
    @NotBlank(message = "숙제 내용을 입력해주세요")
    private String hwContent;

    @Column(name="homework_memo")
    private String hwMemo;

    @Column
    @NotNull(message = "진도를 입력해주세요")
    @Min(value = 0, message = "진도는 음수가 될 수 없습니다.")
    private Integer hwProgress;

    @Column(name="due_date")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Future(message = "제출기한은 지나간 날짜로 설정하실 수 없습니다")
    @NotNull(message = "제출기한을 입력해주세요")
    private Date hwDueDate;

    @Column(name="game_content_title")
    @IsNotNone(message = "숙제 내용을 선택해주세요")
    private String gameTitle;
}
