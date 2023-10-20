package com.idukbaduk.metoo9dan.qna.dto;

import lombok.Data;

@Data
public class AnswerDTOforBatis {
    Integer answer_no;
    Integer question_no;
    String answer_content;
    String answer_title;
    String write_date;
    Integer member_no;

}
