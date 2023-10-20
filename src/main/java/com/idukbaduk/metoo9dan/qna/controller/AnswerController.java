package com.idukbaduk.metoo9dan.qna.controller;

import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.QnaQuestions;
import com.idukbaduk.metoo9dan.member.service.MemberServiceImpl;
import com.idukbaduk.metoo9dan.notice.controller.NoticeController;
import com.idukbaduk.metoo9dan.qna.dto.AnswerDTO;
import com.idukbaduk.metoo9dan.qna.service.AnswerService;
import com.idukbaduk.metoo9dan.qna.service.QuestionService;
import com.idukbaduk.metoo9dan.qna.validation.AnswerForm;
import groovy.util.logging.Log4j2;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;

@Log4j2
@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {
    //looger
    Logger logger = LoggerFactory.getLogger(NoticeController.class);
    private final QuestionService questionService;
    private final MemberServiceImpl memberServiceImpl;
    private final AnswerService answerService;

    //답변 작성
    @PostMapping("/add/{questionNo}")
    public String AnswerAdd(@PathVariable Integer questionNo,
                            Model model,
                            @Valid AnswerForm answerForm,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Principal principal){
        logger.info("questionNo: "+questionNo);
        //문의사항번호 가져오기
        QnaQuestions questions = questionService.getQuestion(questionNo);
        if(bindingResult.hasErrors()){ //에러가 존재하면
            model.addAttribute("question", questions);
            return "qna/qnaDetail"; //화면에서 에러 메시지를 출력함
        }
        //답변자 정보 가져오기
        Member member = memberServiceImpl.getUser(principal.getName());
        //관리자만 답변 작성 가능
        if(!member.getRole().equalsIgnoreCase("admin")){
            redirectAttributes.addFlashAttribute("msg", "답변은 관리자만 작성할 수 있습니다.");
            return String.format("redirect:/qna/detail/%d", questionNo);
        }

        //이미 작성했으면 답변 작성할 수 없어야함. 해당 qNo에 대해 작성된 answer가 있는지 확인. -> inAnswered 확인
        if(questions.getIsAnswered()){
            redirectAttributes.addFlashAttribute("msg", "이미 답변이 등록된 문의사항입니다.");
            return String.format("redirect:/qna/detail/%d", questionNo);
        }

        //비즈니스로직처리(Question의 isAnswered 컬럼 true로 변경)
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setAnswerTitle(answerForm.getTitle());
        answerDTO.setAnswerContent(answerForm.getContent());
        answerDTO.setWriteDate(LocalDateTime.now());
        answerDTO.setMember(member);
        answerDTO.setQnaQuestions(questions);
        answerService.add(answerDTO);

        //Question의 정보도 Update
        answerService.updateIsAnswered(questions);

        //메일발송처리
        //answerService.sendMail(member);

        redirectAttributes.addFlashAttribute("msg", "답변이 등록되었습니다.");
        return String.format("redirect:/qna/detail/%d", questionNo);

        //return String.format("redirect: /qna/detail/%d", questionNo);
    }

    //답변삭제
}
