package com.idukbaduk.metoo9dan.notice.validation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

//공지사항 등록 폼의 입력 값에 대한 유효성 검사
@Getter
@Setter
public class NoticeForm {
    @NotEmpty(message = "은 필수 입력사항입니다.")
    @Size(max=255)
    private String title; //제목
    private String content; //내용
}
