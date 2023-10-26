package com.idukbaduk.metoo9dan.qna.validation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnswerForm {
    @NotEmpty(message = "제목은 필수 입력사항입니다.")
    @Size(max=255)
    private String title; //제목

    @NotEmpty(message = "내용은 필수 입력사항입니다.")
    @NotEmpty
    private String content; //내용
}
