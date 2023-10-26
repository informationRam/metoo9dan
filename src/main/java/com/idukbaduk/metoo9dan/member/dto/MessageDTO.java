package com.idukbaduk.metoo9dan.member.dto;


import lombok.*;

//SMS 발송 대상 및 내용 담기
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MessageDTO {

    private String to;  //발신자
    //String content;   //발신내용 : 인증번호만 보낼거라 필요없음
}