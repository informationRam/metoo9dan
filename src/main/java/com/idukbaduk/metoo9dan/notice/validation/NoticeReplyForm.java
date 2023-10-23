package com.idukbaduk.metoo9dan.notice.validation;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeReplyForm {
    @NotEmpty(message = "댓글 내용이 입력되지 않았습니다.")
    private String content; // 댓글내용

}
