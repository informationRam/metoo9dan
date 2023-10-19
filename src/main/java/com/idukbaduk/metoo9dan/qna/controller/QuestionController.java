package com.idukbaduk.metoo9dan.qna.controller;

import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.QnaQuestions;
import com.idukbaduk.metoo9dan.member.service.MemberServiceImpl;
import com.idukbaduk.metoo9dan.notice.controller.NoticeController;
import com.idukbaduk.metoo9dan.qna.dto.QuestionDTO;
import com.idukbaduk.metoo9dan.qna.service.QuestionService;
import com.idukbaduk.metoo9dan.qna.validation.QuestionForm;
import groovy.util.logging.Log4j2;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

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
        //memberRole에 따른 사이드바 표출을 위한 model 설정
        String memberRole = null;
        if(principal != null){
            memberRole = memberServiceImpl.getUser(principal.getName()).getRole();
        }
        logger.info(memberRole);
        if(principal == null || !memberRole.equalsIgnoreCase("admin")){
            model.addAttribute("memberRole", "notAdmin");
        } else {
            model.addAttribute("memberRole", memberRole);
        }
        //게시글 상세 조회
        QnaQuestions question= questionService.getQuestion(questionNo);
        //파일도 조회할 수 있어야 함.
        //답변도 조회할 수 있어야 함.
        //답변폼은 관리자만 볼 수 있어야 함.
        model.addAttribute("question", question);
        return "qna/qnaDetail";
    }

    /*검색 조회 요청
    * listSize는 10으로 고정.*/
    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/search")
    public String searchQna(Model model, Principal principal, Pageable pageable,
                            @RequestParam(value="page", defaultValue = "0")int pageNo,
                            String searchCategory, String keyword){
        logger.info("searchQna진입");
        logger.info("searchCategory: "+searchCategory);
        logger.info("keyword: "+keyword);
        //memberRole에 따른 사이드바 표출을 위한 model 설정
        String memberRole = null;
        if(principal != null){
            memberRole = memberServiceImpl.getUser(principal.getName()).getRole();
        }
        logger.info(memberRole);
        if(principal == null || !memberRole.equalsIgnoreCase("admin")){
            model.addAttribute("memberRole", "notAdmin");
        } else {
            model.addAttribute("memberRole", memberRole);
        }
        logger.info("memberRole: "+memberRole);

        Page<QnaQuestions> questionPage = questionService.search(pageNo, searchCategory, keyword);
        logger.info("questionPage: "+questionPage);

        int endPage = (int)(Math.ceil((pageNo+1)/5.0))*5; //5의 배수
        int startPage = endPage - 4; //1, 5의 배수 +1 ...
        if(endPage > questionPage.getTotalPages()){ //questionPage null? 검색 후 페이지네이션 이동하려고 하면 에러남
            int realEnd = questionPage.getTotalPages();
            model.addAttribute("endPage", realEnd);
        } else {
            model.addAttribute("endPage", endPage);
        }
        model.addAttribute("questionPage", questionPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("searchCategory", searchCategory);
        model.addAttribute("keyword", keyword); 


        return "qna/qnaList";
    }

    /*문의사항 작성폼 요청
    * /qna/questionAdd */
    @PreAuthorize("isAuthenticated()") //로그인한 사람&&관리자아닌사람만 작성가능
    @GetMapping("/questionAdd")
    public String questionAddForm(QuestionForm questionForm,
                                  Model model,
                                  Principal principal, RedirectAttributes redirectAttributes){
        Member member = memberServiceImpl.getUser(principal.getName());
        //관리자 아닌 사람만 작성 가능해야함
        if(member.getRole().equalsIgnoreCase("admin")){
            redirectAttributes.addFlashAttribute("msg", "관리자는 문의사항을 작성할 수 없습니다.");
            return "redirect:/qna/list"; //목록조회로 튕겨내기
        }
        model.addAttribute("memberRole", member.getRole());
        return "qna/questionForm";
    }

    /*문의사항 작성 처리 요청
     * /qna/questionAdd */
    @PostMapping("/questionAdd")
    @PreAuthorize("isAuthenticated()")//로그인한 사람&&관리자아닌사람만 작성가능
    public String questionAdd(@Valid QuestionForm questionForm, BindingResult bindingResult,
                              @RequestParam("uploadFiles") List<MultipartFile> uploadFiles,
                              RedirectAttributes redirectAttributes,
                              Model model,
                              Principal principal){

        if(bindingResult.hasErrors()){ //에러가 있으면,
            logger.info("Errors: " +bindingResult);
            return "qna/questionForm"; //questionForm.html로 이동.
        }//에러가 없으면, 공지사항 등록 진행

        //1.파라미터받기
            //로그인정보확인
        Member member = memberServiceImpl.getUser(principal.getName());
        //관리자 아닌 사람만.
        if(member.getRole().equalsIgnoreCase("admin")){
            redirectAttributes.addFlashAttribute("msg", "관리자는 문의사항을 작성할 수 없습니다.");
            return "redirect:/qna/list"; //목록조회로 튕겨내기
        }

        //2.비즈니스로직수행
        //유효성검사를 마친 Form 데이터를 DTO에 담기
        QuestionDTO questionDTO = new QuestionDTO();

        //3.모델
        //뷰

        return null;
        //return String.format("redirect:/qan/detail/%d", questionNo); //상세조회로 이동
    }

}
