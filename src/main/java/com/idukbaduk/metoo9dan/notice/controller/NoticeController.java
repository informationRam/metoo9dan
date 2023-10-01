package com.idukbaduk.metoo9dan.notice.controller;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeReply;
import com.idukbaduk.metoo9dan.notice.service.NoticeService;
import com.idukbaduk.metoo9dan.notice.validation.NoticeForm;
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
                                  Model model){
        Notice notice = noticeService.getNotice(noticeNo);
        if(notice!=null){
            noticeService.readCntUp(noticeNo);
        }
        List<NoticeReply> noticeReply = noticeService.getNoticeReply(notice);
        model.addAttribute("notice", notice);
        model.addAttribute("noticeReply", noticeReply);
        return "notice/noticeDetail";
    }

    //작성폼 보여줘 요청
    @GetMapping("/add")
    public String noticeAddForm(NoticeForm noticeForm){

        return "notice/noticeForm";
    }

    //공지사항 등록 처리해줘 요청 (임시)
    @PostMapping("/add")
    public String add(@Valid NoticeForm noticeForm,
                      BindingResult bindingResult
                      ){
        if(!bindingResult.hasErrors()){
            noticeService.add(noticeForm.getTitle(),
                              noticeForm.getContent());

        } else {
            return "/notice/noticeForm"; //에러가 있으면, noticeForm.html로 이동.
        }
        return "redirect:/notice/list"; //질문목록조회 요청
    }
}
