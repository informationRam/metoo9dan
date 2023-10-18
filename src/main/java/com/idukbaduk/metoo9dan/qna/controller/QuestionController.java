package com.idukbaduk.metoo9dan.qna.controller;

import com.idukbaduk.metoo9dan.common.entity.QnaQuestions;
import com.idukbaduk.metoo9dan.member.service.MemberServiceImpl;
import com.idukbaduk.metoo9dan.notice.controller.NoticeController;
import com.idukbaduk.metoo9dan.qna.service.QuestionService;
import groovy.util.logging.Log4j2;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.security.Principal;

@Log4j2
@RequestMapping("/qna")
@RequiredArgsConstructor
@Controller
public class QuestionController {
    //looger
    Logger logger = LoggerFactory.getLogger(NoticeController.class);

    private final MemberServiceImpl memberServiceImpl;
    private final QuestionService questionService;

    //method
    /* 문의사항 목록 보여줘 요청
     * model로 memberRole 넘겨주어야 함.*/
    @GetMapping("/list")
    public String getQnaList(Model model,
                             //Pageable pageable,
                             Principal principal,
                             @RequestParam(value = "page", defaultValue = "0")int pageNo){
        String memberRole = null;
        if(principal != null){
            memberRole = memberServiceImpl.getUser(principal.getName()).getRole();
        }
        if(principal == null || !memberRole.equalsIgnoreCase("admin")){
            model.addAttribute("memberRole", "notAdmin");
        } else {
            model.addAttribute("memberRole", memberRole);
        }

        Page<QnaQuestions> questionPage = questionService.getList(pageNo);
        logger.info("questionPage: "+questionPage);
        int endPage = (int)(Math.ceil((pageNo+1)/5.0))*5; //5의 배수
        int startPage = endPage - 4; //1, 5의 배수 +1 ...
        if(endPage > questionPage.getTotalPages()){
            int realEnd = questionPage.getTotalPages();
            model.addAttribute("endPage", realEnd);
        } else {
            model.addAttribute("endPage", endPage);
        }

        model.addAttribute("questionPage", questionPage);
        model.addAttribute("startPage", startPage);

        return "qna/qnaList";
    }

    /*상세조회 요청*/
    @GetMapping("/detail/{questionNo}")
    public String getQuestionDetail(@PathVariable Integer questionNo,
                                    Principal principal,
                                    Model model){
        return "qna/qnaDetail";
    }

    /*문의사항 작성폼 요청
    * /qna/questionAdd */
    @GetMapping("/questionAdd")
    public String questionAddForm(){
        return "";
    }

    /*문의사항 작성 처리 요청
     * /qna/questionAdd */
    @PostMapping("/questionAdd")
    public String questionAdd(){
        return "";
    }

}
