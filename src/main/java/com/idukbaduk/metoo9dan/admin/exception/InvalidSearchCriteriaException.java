package com.idukbaduk.metoo9dan.admin.exception;


//회원 검색 조건 잘못됐을 경우 예외처리 (검색결과 없을때)
public class InvalidSearchCriteriaException extends RuntimeException {
    public InvalidSearchCriteriaException(String message) {
        super(message);
    }
}
