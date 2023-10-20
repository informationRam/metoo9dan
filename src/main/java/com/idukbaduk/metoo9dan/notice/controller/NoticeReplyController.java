package com.idukbaduk.metoo9dan.notice.controller;

import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeReply;
import com.idukbaduk.metoo9dan.member.service.MemberServiceImpl;
import com.idukbaduk.metoo9dan.notice.service.NoticeReplyService;
import com.idukbaduk.metoo9dan.notice.service.NoticeService;
import com.idukbaduk.metoo9dan.notice.validation.NoticeReplyForm;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

//공지사항 댓글에 대한 요청을 처리하는 컨트롤러 클래스
@Controller
@RequiredArgsConstructor
@RequestMapping("/reply")
public class NoticeReplyController {

    //필드
    private final NoticeService noticeService;
    private final NoticeReplyService replyService;
    private final MemberServiceImpl memberServiceImpl;
    //생성자

    //메소드
    // 댓글 등록처리 해줘 요청
    @PostMapping("/add/{noticeNo}")
    public String replyAdd(@PathVariable("noticeNo")Integer noticeNo,
                           Model model,
                           @Valid NoticeReplyForm noticeReplyForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Principal principal
                           ){
        // 1.파라미터 받기
        Notice notice = noticeService.getNotice(noticeNo);
        Member member = memberServiceImpl.getUser(principal.getName());
            //유저정보도가져와야함

        // 2.비즈니스로직 수행
        if(bindingResult.hasErrors()){ //에러가 존재하면
            model.addAttribute("notice", notice);
            return "notice/noticeDetail"; //화면에서 에러 메시지를 출력함
        } else { //에러 없으면
            replyService.add(notice, noticeReplyForm.getContent(), member); // 유효성 검사 거친 내용으로 저장처리 (user정보 추가해야함)
            redirectAttributes.addFlashAttribute("msg", "댓글이 등록되었습니다.");
            return String.format("redirect:/notice/detail/%d", noticeNo);
        }

        // 3.모델
        // 4.뷰
    }

    // 댓글 수정처리 해줘 요청
    //권한 있는지 확인하는 어노테이션 추가해야함. @PreAuthorize("isAuthenticated()")
    @PutMapping ("/modify/{replyNo}")
    @ResponseBody
    public String replyModify(@RequestBody NoticeReplyForm noticeReplyForm,
                              @PathVariable Integer replyNo){
        replyService.updateReply(replyNo, noticeReplyForm.getContent());
        Integer noticeNo = replyService.getReply(replyNo).getNotice().getNoticeNo();
        return String.format("redirect:/notice/detail/%d", noticeNo);
    }

    //써놓은 댓글 보여줘 요청
    @GetMapping("/get/{replyNo}")
    @ResponseBody
    public ResponseEntity<String> getReply(@PathVariable Integer replyNo){
        try {
            NoticeReply reply = replyService.getReply(replyNo);
            String content = reply.getContent();
            return ResponseEntity.ok().body("{\"content\": \"" + content + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal Server Error\"}");
        }
    }

    // 댓글 삭제 요청
    @GetMapping("/delete/{replyNo}")
    public String delete(@PathVariable Integer replyNo, 
                         RedirectAttributes redirectAttributes,
                         Principal principal){
        Member member = memberServiceImpl.getUser(principal.getName());
        //우선 삭제할 댓글이 존재하는지 조회하여 확인
        NoticeReply noticeReply = replyService.getReply(replyNo);
        Integer noticeNo =replyService.getReply(replyNo).getNotice().getNoticeNo();
        //댓글 작성자와 로그인한 유저가 같지 않는 경우 BAD_REQUEST 응답처리하는 코드 추가해야함
        if(!member.getMemberId().equals(principal.getName())){
            redirectAttributes.addFlashAttribute("msg", "댓글을 삭제할 권한이 없습니다.");
            return String.format("redirect:/notice/detail/%d", noticeNo);
        }
        replyService.delete(noticeReply);
        return String.format("redirect:/notice/detail/%d", noticeNo);
    }
}
