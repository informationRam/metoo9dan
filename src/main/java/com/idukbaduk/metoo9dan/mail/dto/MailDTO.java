package com.idukbaduk.metoo9dan.mail.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MailDTO {
    private String to;
    private String address;
    private String title;
    private String content;
}
