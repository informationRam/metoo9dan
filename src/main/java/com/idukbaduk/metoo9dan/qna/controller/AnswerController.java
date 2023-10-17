package com.idukbaduk.metoo9dan.qna.controller;

import com.idukbaduk.metoo9dan.notice.controller.NoticeController;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {
    //looger
    Logger logger = LoggerFactory.getLogger(NoticeController.class);
}
