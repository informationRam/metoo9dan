package com.idukbaduk.metoo9dan.homework.domain;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Data
@RequiredArgsConstructor
@ToString
public class HomeworkSubmitDetailDTO {
    private Integer homeworkNo;
    private Integer sendNo;
    private Integer homeworkSubmitNo;
    private String homeworkTitle;
    private String homeworkContent;
    private String memberName;//교육자명
    private Integer progress;
    private Date dueDate;
    private String content;
    private String additionalQuestion;
}

