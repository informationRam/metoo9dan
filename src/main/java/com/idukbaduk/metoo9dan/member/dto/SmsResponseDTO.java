package com.idukbaduk.metoo9dan.member.dto;

import lombok.*;

import java.time.LocalDateTime;

//응답 결과
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SmsResponseDTO {
    String requestId;  //요청ID
    LocalDateTime requestTime;  //요청시간:yyyy-MM-dd'T'HH:mm:ss.SSS
    String statusCode;   //요청상태코드 : 202성공, 그 외 실패
    String statusName;   //요청상태명 : success or fail

}