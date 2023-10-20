package com.idukbaduk.metoo9dan.notice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
//조회된 결과가 없을 경우 발생하는 예외 처리 클래스

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "해당 게시글이 존재하지 않습니다.")
public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
