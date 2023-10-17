package com.idukbaduk.metoo9dan.qna.controller;

import com.idukbaduk.metoo9dan.notice.controller.NoticeController;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@RequestMapping("/qna")
@RequiredArgsConstructor
@Controller
public class QuestionController {
    //looger
    Logger logger = LoggerFactory.getLogger(NoticeController.class);

    //method
    //문의사항 목록 보여줘 요청
    //model로 memberRole 넘겨주어야 함.
    @GetMapping("/list")
    public String getQnaList(){
        return "qna/qnaList";
    }
}
