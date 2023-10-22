package com.idukbaduk.metoo9dan.mail.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailDTO {
    private String from; // 보내는 사람(운영자)
    private String address;
    private String title;
    private String content;
}
