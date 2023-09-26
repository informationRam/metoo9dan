package com.idukbaduk.metoo9dan.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

//(스프링시큐리티가) 인증 후 사용자에게 부여할 권한을 정의한 클래스
//'admin', 'educator', 'student', 'pendingapproval'
@AllArgsConstructor
@Getter
public enum Role {
    ADMIN("ROLE_ADMIN"), EDUCATOR("ROLE_EDUCATOR"),STUDENT("ROLE_STUDENT"),PENDINGAPPROVAL("ROLE_PENDINGAPPROVAL");

    private String value;

    public String getValue() {
        return value;
    }
}
