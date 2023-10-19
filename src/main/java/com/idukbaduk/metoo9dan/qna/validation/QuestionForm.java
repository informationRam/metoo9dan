package com.idukbaduk.metoo9dan.qna.validation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

//문의사항 작성폼의 유효성검사를 위한 클래스
@Data
public class QuestionForm {
    @NotEmpty(message = "제목은 필수 입력사항입니다.")
    @Size(max=255)
    private String title; //제목

    @NotEmpty(message = "내용은 필수 입력사항입니다.")
    @NotEmpty
    private String content; //내용

    private LocalDateTime writeDate; //작성일

    private Boolean isAnswered = false; //답변여부: 기본값 false

    private List<MultipartFile> originFiles; //원본파일명
}
