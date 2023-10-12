package com.idukbaduk.metoo9dan.member.validation;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginVaildation{

    @NotEmpty(message = "아이디는 필수입력입니다.")
    private String memberId;            //'회원 아이디'

    @NotEmpty(message = "비밀번호는 필수입력입니다.")
    private String password;                //'비밀번호'

}