package com.idukbaduk.metoo9dan.qna.controller;

import com.idukbaduk.metoo9dan.common.entity.*;
import com.idukbaduk.metoo9dan.member.service.MemberServiceImpl;
import com.idukbaduk.metoo9dan.notice.controller.NoticeController;
import com.idukbaduk.metoo9dan.notice.dto.NoticeFileDTO;
import com.idukbaduk.metoo9dan.notice.validation.NoticeForm;
import com.idukbaduk.metoo9dan.qna.dto.AnswerDTOforBatis;
import com.idukbaduk.metoo9dan.qna.dto.QuestionDTO;
import com.idukbaduk.metoo9dan.qna.dto.QuestionFileDTO;
import com.idukbaduk.metoo9dan.qna.service.AnswerService;
import com.idukbaduk.metoo9dan.qna.service.QuestionFilesService;
import com.idukbaduk.metoo9dan.qna.service.QuestionService;
import com.idukbaduk.metoo9dan.qna.validation.AnswerForm;
import com.idukbaduk.metoo9dan.qna.validation.QuestionForm;
import groovy.util.logging.Log4j2;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@RequestMapping("/qna")
@RequiredArgsConstructor
@Controller
public class QuestionController {
    //looger
    Logger logger = LoggerFactory.getLogger(NoticeController.class);

    private final MemberServiceImpl memberServiceImpl;
    private final QuestionService questionService;
    private final QuestionFilesService filesService;
    private final AnswerService answerService;

    //method
    /*관리자아닌사람.
     * 자기가 작성한 문의사항만 목록조회할 수 있어야 함.
     * 요청주소를 따로 만들지? service단에서 불러오는 페이지를 제한할지?*/


    /* 관리자용
     * 문의사항 목록 보여줘 요청
     * model로 memberRole 넘겨주어야 함.*/
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String getQnaList(Model model,
                             Principal principal,
                             @RequestParam(value = "page", defaultValue = "0") int pageNo) {
        Member member = memberServiceImpl.getUser(principal.getName());
        Page<QnaQuestions> questionPage = null;

        if (!member.getRole().equalsIgnoreCase("admin")) { //관리자 아닌경우
            questionPage = questionService.getMyQnaList(pageNo, member);
            model.addAttribute("memberRole", "notAdmin");
        } else { //관리자인 경우
            //관리자용
            questionPage = questionService.getList(pageNo);
            model.addAttribute("memberRole", member.getRole());
        }

        logger.info("questionPage: " + questionPage);

        model.addAttribute("questionPage", questionPage);
        int endPage = (int) (Math.ceil((pageNo + 1) / 5.0)) * 5; //5의 배수
        int startPage = endPage - 4; //1, 5의 배수 +1 ...
        if (endPage > questionPage.getTotalPages()) {
            int realEnd = questionPage.getTotalPages();
            model.addAttribute("endPage", realEnd);
        } else {
            model.addAttribute("endPage", endPage);
        }
        model.addAttribute("startPage", startPage);

        return "qna/qnaList";
    }

    /*상세조회 요청*/
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/detail/{questionNo}")
    public String getQuestionDetail(@PathVariable("questionNo") Integer questionNo,
                                    AnswerForm answerForm,
                                    Principal principal,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        Member member = memberServiceImpl.getUser(principal.getName());
        QnaQuestions questions = questionService.getQuestion(questionNo);
        //본인이 작성한 글이거나 관리자여야 열람이 가능함.
        // 관리자가 아니고, 본인이 작성한 글이 아닐 때는 열람할 수 없음
        if(!member.getRole().equalsIgnoreCase("admin") && !questions.getMember().equals(member)){
            redirectAttributes.addFlashAttribute("msg", "해당 게시글을 조회할 권한이 없습니다.");
            return "redirect:/qna/list";
        }

        //memberRole에 따른 사이드바 표출을 위한 model 설정
        String memberRole = null;
        if (principal != null) {
            memberRole = memberServiceImpl.getUser(principal.getName()).getRole();
        }
        logger.info(memberRole);
        if (principal == null || !memberRole.equalsIgnoreCase("admin")) {
            model.addAttribute("memberRole", "notAdmin");
        } else {
            model.addAttribute("memberRole", memberRole);
        }
        //파일도 조회할 수 있어야 함.
        List<QuestionFiles> filesList = filesService.getFiles(questions);
        QnaAnswers answer = questions.getQnaAnswers();
        logger.info("Q.getA(): "+answer);

        //답변폼은 관리자만 볼 수 있어야 함.
        model.addAttribute("question", questions);
        //model.addAttribute("answer", answers);
        model.addAttribute("filesList", filesList);
        return "qna/qnaDetail";
    }

    /*검색 조회 요청
     * listSize는 10으로 고정.*/
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public String searchQna(Model model, Principal principal, Pageable pageable,
                            @RequestParam(value = "page", defaultValue = "0") int pageNo,
                            String searchCategory, String keyword) {
        logger.info("searchQna진입");
        logger.info("searchCategory: " + searchCategory);
        logger.info("keyword: " + keyword);
        //memberRole에 따른 사이드바 표출을 위한 model 설정
        Member member = memberServiceImpl.getUser(principal.getName());
        Page<QnaQuestions> questionPage = null;

        if (!member.getRole().equalsIgnoreCase("admin")) { //관리자 아닌경우
            questionPage = questionService.searchMyQnaList(pageNo, searchCategory, keyword, member);
            model.addAttribute("memberRole", "notAdmin");
        } else { //관리자인 경우
            //관리자용
            questionPage = questionService.search(pageNo, searchCategory, keyword);
            model.addAttribute("memberRole", member.getRole());
        }

        logger.info("questionPage: " + questionPage);

        int endPage = (int) (Math.ceil((pageNo + 1) / 5.0)) * 5; //5의 배수
        int startPage = endPage - 4; //1, 5의 배수 +1 ...
        if (endPage > questionPage.getTotalPages()) { //questionPage null? 검색 후 페이지네이션 이동하려고 하면 에러남
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
                                  Principal principal, RedirectAttributes redirectAttributes) {
        Member member = memberServiceImpl.getUser(principal.getName());
        //관리자 아닌 사람만 작성 가능해야함
        if (member.getRole().equalsIgnoreCase("admin")) {
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
                              Principal principal) {

        if (bindingResult.hasErrors()) { //에러가 있으면,
            logger.info("Errors: " + bindingResult);
            return "qna/questionForm"; //questionForm.html로 이동.
        }//에러가 없으면, 공지사항 등록 진행

        //로그인정보확인
        Member member = memberServiceImpl.getUser(principal.getName());
        //관리자 아닌 사람만.
        if (member.getRole().equalsIgnoreCase("admin")) {
            redirectAttributes.addFlashAttribute("msg", "관리자는 문의사항을 작성할 수 없습니다.");
            return "redirect:/qna/list"; //목록조회로 튕겨내기
        }

        //유효성검사를 마친 Form 데이터를 DTO에 담기
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setQuestionTitle(questionForm.getTitle());
        questionDTO.setQuestionContent(questionForm.getContent());
        questionDTO.setMember(member);
        questionDTO.setWriteDate(LocalDateTime.now());
        questionDTO.setIsAnswered(false);

        QnaQuestions question = questionService.add(questionDTO);

        //파일업로드 로직
        //파일업로드(물리적 폴더에 저장)
        String uploadFolder = "C:/upload";
        List<QuestionFileDTO> list = new ArrayList<>();

        //파일업로드처리(DB NoticeFiles 테이블에 저장)
        for (MultipartFile multipartFile : uploadFiles) {
            if (!multipartFile.isEmpty()) {
                if (!filesService.fileSizeOk(multipartFile) || !filesService.fileTypeOk(multipartFile)) {
                    model.addAttribute("msg", "파일을 업로드할 수 없습니다. 파일 확장자와 사이즈를 확인하세요.");
                    return "qna/questionForm"; //questionForm.html로 이동.
                }
            }
            filesService.fileUpload(uploadFolder, question, multipartFile, list, redirectAttributes);
        }//파일 없으면,
        filesService.addFiles(list);

        redirectAttributes.addFlashAttribute("msg", "문의사항이 등록되었습니다.");

        return String.format("redirect:/qna/detail/%d", question.getQuestionNo()); //상세조회로 이동
    }

    /* 수정 폼 조회 요청
     * 답변이 있으면 수정 폼 조회 안됨
    */
    @GetMapping("/questionModify/{questionNo}")
    @PreAuthorize("isAuthenticated()")
    public String questionModifyForm(@PathVariable Integer questionNo,
                                     QuestionForm questionForm,
                                     Principal principal,
                                     Model model, 
                                     RedirectAttributes redirectAttributes) {
        //수정하려는 사람이 작성자인지 확인
        Member member = memberServiceImpl.getUser(principal.getName());
        //문의사항 조회
        QnaQuestions questions = questionService.getQuestion(questionNo);
        logger.info("가져온 question: "+questions);
        List<QuestionFiles> filesList = filesService.getFiles(questions);
        if(!questions.getMember().equals(member)){
            redirectAttributes.addFlashAttribute("msg","수정할 권한이 없습니다.");
            return String.format("redirect:/qna/detail/%d",questionNo);
        }
        //해당 문의사항에 대한 답변이 있는지 확인
        if(questions.getIsAnswered()){
            redirectAttributes.addFlashAttribute("msg","답변이 등록되어 수정할 수 없습니다.");
            return String.format("redirect:/qna/detail/%d",questionNo);
        }
        //조회된 문의사항 내용을 수정폼에 세팅
        questionForm.setTitle(questions.getQuestionTitle());
        questionForm.setContent(questions.getQuestionContent());

        model.addAttribute("question", questions);
        model.addAttribute("filesList", filesList);
        model.addAttribute("memberRole", member.getRole()); //for 사이드바
        return "qna/questionModifyForm";
    }
    /*수정 처리 요청
    * 답변이 있으면 수정 처리하면 안됨*/
    @PostMapping("/questionModify/{questionNo}")
    public String modify(@PathVariable Integer questionNo,
                         @RequestParam List<MultipartFile> uploadFiles,
                         @Valid QuestionForm questionForm, //유효성검사처리를 해주어야함
                         BindingResult bindingResult, //유효성검사의 결과
                         RedirectAttributes redirectAttributes,
                         Principal principal, Model model){
        //수정하려는 사람이 작성자인지 확인
        Member member = memberServiceImpl.getUser(principal.getName());
        QnaQuestions questions = questionService.getQuestion(questionNo);
        if(bindingResult.hasErrors()){ //에러가 있으면,
            logger.info("Errors: " +bindingResult);
            return "notice/noticeModifyForm"; //noticeModifyForm.html로 이동.
        }//에러가 없으면, 공지사항 등록 진행
        //수정 로직
        List<QuestionFiles> filesList = filesService.getFiles(questions); //파일조회
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setQuestionTitle(questionForm.getTitle());
        questionDTO.setQuestionContent(questionForm.getContent());
        questionService.modify(questions, questionDTO);

        List<QuestionFileDTO> list = new ArrayList<>();
        //파일업로드(물리적 폴더에 저장)
        String uploadFolder = "C:/upload";
        //파일업로드처리(DB NoticeFiles 테이블에 저장)
        for(MultipartFile multipartFile : uploadFiles) {
            if (!multipartFile.isEmpty()) {
                if (!filesService.fileSizeOk(multipartFile) || !filesService.fileTypeOk(multipartFile)) {
                    model.addAttribute("msg","파일을 업로드할 수 없습니다. 파일 확장자와 사이즈를 확인하세요.");
                    model.addAttribute("question", questions);
                    model.addAttribute("filesList", filesList); //공지 파일 목록
                    return "qna/questionModifyForm"; //questionModifyForm.html로 이동.
                }
                filesService.fileUpload(uploadFolder, questions, multipartFile, list, redirectAttributes);
            }
        }//파일 없으면,
        filesService.addFiles(list);

        redirectAttributes.addFlashAttribute("msg", "문의사항 게시글이 수정되었습니다.");
        return String.format("redirect:/qna/detail/%d", questionNo);
    }
    
    // 문의사항 게시글 삭제 요청
    @GetMapping("/delete/{questionNo}")
    public String delete(@PathVariable Integer questionNo,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        logger.info("delete진입");
        //삭제하려는 사람이 작성자인지 확인
        Member member = memberServiceImpl.getUser(principal.getName());
        //삭제할 게시글 조회
        QnaQuestions questions = questionService.getQuestion(questionNo);
        logger.info("가져온 question: "+questions);
        List<QuestionFiles> filesList = filesService.getFiles(questions);
        if(!questions.getMember().equals(member)){
            redirectAttributes.addFlashAttribute("msg","삭제할 권한이 없습니다.");
            return String.format("redirect:/qna/detail/%d",questionNo);
        }
        //해당 문의사항에 대한 답변이 있는지 확인
        if(questions.getIsAnswered()){
            redirectAttributes.addFlashAttribute("msg","답변이 등록되어 삭제할 수 없습니다.");
            return String.format("redirect:/qna/detail/%d",questionNo);
        }
        //삭제로직
        questionService.delete(questions);
        //디스크에 있는 파일 삭제
        for(QuestionFiles file : filesList){
            filesService.deleteOnDisk(file); //디스크에서 삭제처리
        }
        return "redirect:/qna/list";
    }

}