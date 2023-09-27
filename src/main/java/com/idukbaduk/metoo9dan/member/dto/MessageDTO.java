package com.idukbaduk.metoo9dan.member.dto;


import lombok.*;

//SMS 내용 담기
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MessageDTO {
    String to;  //발신자
    String content;   //발신내용
}