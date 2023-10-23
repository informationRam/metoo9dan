package com.idukbaduk.metoo9dan.homework.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@RequiredArgsConstructor
@ToString
public class HomeworkSubmitDTO {
    private Integer homeworkNo;
    private Integer sendNo;
    private String content;
    private String additionalQuestion;
}