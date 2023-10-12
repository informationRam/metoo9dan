package com.idukbaduk.metoo9dan.member.security;

import lombok.Getter;

@Getter // 상수 자료형이기 때문에 @Setter 는 선언하지 않았다.
public enum Role { // 열거 자료형 enum 을 사용

    ADMIN("ADMIN"),
    NORMAL("NORMAL"),
    EDUCATOR("EDUCATOR"),
    STUDENT("STUDENT");

    private String value;

    Role(String value) {
        this.value = value;
    }
}