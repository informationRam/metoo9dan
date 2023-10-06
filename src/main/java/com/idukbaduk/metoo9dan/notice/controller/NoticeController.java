package com.idukbaduk.metoo9dan.notice.controller;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeReply;
import com.idukbaduk.metoo9dan.notice.service.NoticeService;
import com.idukbaduk.metoo9dan.notice.validation.NoticeForm;
import com.idukbaduk.metoo9dan.notice.validation.NoticeReplyForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 공지사항 관련 요청을 담당하는 컨트롤러
@RequestMapping("/notice")
@RequiredArgsConstructor
@Controller
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 등록 메뉴를 누르면 공지사항 목록을 보여줌
    @GetMapping("/list")
    public String getNoticeList(Model model,
                                @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                                @RequestParam(value = "listSize", defaultValue = "10") int listSize
                                ){

        Page<Notice> noticePage = this.noticeService.getList(pageNo, listSize);
        model.addAttribute("noticePage", noticePage);
        return "notice/noticeList";
    }

    // 상세조회 (+댓글 목록조회 및 작성 폼)
    @GetMapping("/detail/{noticeNo}")
    public String getNoticeDetail(@PathVariable("noticeNo")Integer noticeNo,
                                  NoticeReplyForm noticeReplyForm,
                                  Model model){
        Notice notice = noticeService.getNotice(noticeNo);
        if(notice!=null){
            noticeService.readCntUp(noticeNo);
        }
        List<NoticeReply> noticeReply = noticeService.getNoticeReply(notice);
        model.addAttribute("notice", notice); //공지상세 내용
        model.addAttribute("noticeReply", noticeReply); //공지 댓글 목록
        return "notice/noticeDetail";
    }

    //공지사항 삭제해줘 요청
    @GetMapping("/delete/{noticeNo}")
    public String delete(@PathVariable Integer noticeNo){
        // 1. 파라미터 받기
        // 2. 비즈니스 로직 처리
        //우선 삭제할 글이 존재하는지 조회하여 확인
        Notice notice = noticeService.getNotice(noticeNo);
        //댓글 작성자와 로그인한 유저가 같지 않는 경우 BAD_REQUEST 응답처리하는 코드 추가해야함
        /*if(member.~~~.equals(principal.getName())){

        }*/
        noticeService.delete(notice);
        // 3. 모델
        // 4. 뷰
        return "redirect:/notice/list";
    }

    //공지 작성폼 보여줘 요청
    @GetMapping("/add")
    public String noticeAddForm(NoticeForm noticeForm){
        return "notice/noticeForm";
    }

    //공지사항 등록 처리해줘 요청 (임시)
    @PostMapping("/add")
    public String add(@Valid NoticeForm noticeForm,
                      BindingResult bindingResult
                      ){
        if(bindingResult.hasErrors()){
            return "/notice/noticeForm"; //에러가 있으면, noticeForm.html로 이동.

        } else { //에러가 없으면, 공지사항 등록 진행
            noticeService.add(noticeForm.getTitle(),
                              noticeForm.getContent());
            return "redirect:/notice/list"; //질문목록조회 요청
        }
    }


}
