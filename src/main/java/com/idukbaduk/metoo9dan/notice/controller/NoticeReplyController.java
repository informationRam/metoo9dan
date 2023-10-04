package com.idukbaduk.metoo9dan.notice.controller;

import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.notice.service.NoticeReplyService;
import com.idukbaduk.metoo9dan.notice.service.NoticeService;
import com.idukbaduk.metoo9dan.notice.validation.NoticeReplyForm;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//공지사항 댓글에 대한 요청을 처리하는 컨트롤러 클래스
@Controller
@RequiredArgsConstructor
@RequestMapping("/reply")
public class NoticeReplyController {

    //필드
    private final NoticeService noticeService;
    private final NoticeReplyService replyService;
    //생성자

    //메소드
    // 댓글 등록처리 해줘 요청
    @PostMapping("/add/{noticeNo}")
    public String replyAdd(@PathVariable("noticeNo")Integer noticeNo,
                         Model model,
                         @Valid NoticeReplyForm noticeReplyForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes
                         ){
        // 1.파라미터 받기
        Notice notice = noticeService.getNotice(noticeNo);
            //유저정보도가져와야함

        // 2.비즈니스로직 수행
        if(bindingResult.hasErrors()){ //에러가 존재하면
            model.addAttribute("notice", notice);
            return "notice/noticeDetail"; //화면에서 에러 메시지를 출력함
        } else { //에러 없으면
            //임시로 memberNo 1인 녀석을 넣어줄거임
            Member member = new Member();
            member.setMemberNo(1);
            replyService.add(notice, noticeReplyForm.getContent(), member); // 유효성 검사 거친 내용으로 저장처리 (user정보 추가해야함)
            redirectAttributes.addFlashAttribute("msg", "댓글이 등록되었습니다.");
            return String.format("redirect:/notice/detail/%d", noticeNo);
        }

        // 3.모델
        // 4.뷰
    }

    // 댓글 수정 폼 보여줘 요청
    //권한 있는지 확인하는 어노테이션 추가해야함. @PreAuthorize("isAuthenticated()")
    @PatchMapping ("/modify/{replyNo}")
    public String replyModifyForm(NoticeReplyForm noticeReplyForm,
                                  @PathVariable Integer replyNo){
        //1. 파라미터받기
        //2. 비즈니스로직수행
        //3. 모델
        //4. 뷰
        return "notice/noticeDetail";
    }

    // 댓글 수정 처리해줘 요청
    /*/reply/modify/${reply.noticeReplyNo}
    public String replyModify(){

    }*/
}
