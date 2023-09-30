package com.idukbaduk.metoo9dan.member.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SmsRequestDTO {
    String type;  //SMS, LMS, MMS 인지 설정, 여기선 SMS(필수)
    String contentType;  //COMM 또는 AD : 여기선 COMM(기본)
    String countryCode;
    String from;         //발신자(필수)
    String content;      //기본 메세지 내용
    List<MessageDTO> messages;   //발신 메세지 정보(to, content 등)

}
