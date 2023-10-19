package com.idukbaduk.metoo9dan.qna.dto;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.QnaQuestions;
import lombok.Data;

@Data
public class QuestionFileDTO {
    private QnaQuestions questions; //문의 번호(정보)
    private String originFileName; //원본파일명
    private String uuid; //사본파일명
    private String uploadPath; //파일경로
}
