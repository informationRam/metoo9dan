package com.idukbaduk.metoo9dan.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VerificationRequest {
    private String memName;
    private String to;
    private String verificationCode;

}
