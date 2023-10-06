package com.idukbaduk.metoo9dan.notice.validation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//공지사항 등록 폼의 입력 값에 대한 유효성 검사
@Getter
@Setter
public class NoticeForm {
    @NotEmpty
    private String noticeType;          //공지 타입(공지사항, 자주묻는질문)

    @NotEmpty(message = "은 필수 입력사항입니다.")
    @Size(max=255)
    private String title; //제목

    @NotEmpty
    private String content; //내용

    private LocalDateTime writeDate; //작성일
    private LocalDateTime postDate; //게시일

    @NotEmpty
    private String status;              //상태(게시, 예약)

    @NotNull
    private Boolean isImp = false;      //중요게시글 여부 -기본값 false
}
