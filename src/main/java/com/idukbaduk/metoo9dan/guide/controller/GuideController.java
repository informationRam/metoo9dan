package com.idukbaduk.metoo9dan.guide.controller;

import com.idukbaduk.metoo9dan.notice.controller.NoticeController;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@RequestMapping("/guide")
@RequiredArgsConstructor
@Controller
public class GuideController {
    //logger
    Logger logger = LoggerFactory.getLogger(NoticeController.class);

    //field

    //method

    /* 사이트소개 페이지 접근시
        /guide/siteIntro */
    @GetMapping("/siteIntro")
    public String showSiteIntroduce(){
        return "guide/siteIntroduce";
    }
}
