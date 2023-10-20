package com.idukbaduk.metoo9dan.notice.controller;

import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeFiles;
import com.idukbaduk.metoo9dan.common.entity.NoticeReply;
import com.idukbaduk.metoo9dan.member.service.MemberServiceImpl;
import com.idukbaduk.metoo9dan.notice.dto.NoticeDTO;
import com.idukbaduk.metoo9dan.notice.dto.NoticeFileDTO;
import com.idukbaduk.metoo9dan.notice.service.NoticeFilesService;
import com.idukbaduk.metoo9dan.notice.service.NoticeService;
import com.idukbaduk.metoo9dan.notice.validation.NoticeForm;
import com.idukbaduk.metoo9dan.notice.validation.NoticeReplyForm;
import groovy.util.logging.Log4j2;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// 공지사항 관련 요청을 담당하는 컨트롤러
@Log4j2
@RequestMapping("/notice")
@RequiredArgsConstructor
@Controller
public class NoticeController {
    //looger
    Logger logger = LoggerFactory.getLogger(NoticeController.class);

    private final NoticeService noticeService;
    private final NoticeFilesService filesService;
    private final MemberServiceImpl memberServiceImpl;

    // 공지사항 등록 메뉴를 누르면 공지사항 목록을 보여줌
    // 페이지네이션 추가
    @GetMapping("/list")
    public String getNoticeList(Model model, Pageable pageable, Principal principal,
                                @RequestParam(value = "page", defaultValue = "0") int pageNo,
                                @RequestParam(value = "listSize", defaultValue = "10") int listSize){
        Page<Notice> noticePage = null;

        //memberRole에 따른 사이드바 표출을 위한 model 설정
        String memberRole = null;
        if(principal != null){
            memberRole = memberServiceImpl.getUser(principal.getName()).getRole();
        }
        logger.info(memberRole);

        if(principal == null || !memberRole.equalsIgnoreCase("admin")){
            noticePage = this.noticeService.getList(pageNo, listSize);
            model.addAttribute("memberRole", "notAdmin");
        } else {
            noticePage = this.noticeService.getAdminList(pageNo, listSize);
            model.addAttribute("memberRole", memberRole);
        }

        int endPage = (int)(Math.ceil((pageNo+1)/5.0))*5; //5의 배수
        int startPage = endPage - 4; //1, 5의 배수 +1 ...
        if(endPage > noticePage.getTotalPages()){
            int realEnd = noticePage.getTotalPages();
            model.addAttribute("endPage", realEnd);
        } else {
            model.addAttribute("endPage", endPage);
        }

        model.addAttribute("noticePage", noticePage);
        model.addAttribute("startPage", startPage);
        return "notice/noticeList";
    }

    //검색
    @GetMapping("/search")
    public String getNoticeList(Model model, Principal principal, Pageable pageable,
                                @RequestParam(value = "page", defaultValue = "0") int pageNo,
                                @RequestParam(value = "listSize", defaultValue = "10") int listSize,
                                String noticeType, String status, String searchCategory, String keyword){

        logger.info("noticeType: "+noticeType);
        logger.info("status: "+status);
        Page<Notice> noticePage = null;

        //memberRole에 따른 사이드바 표출을 위한 model 설정
        String memberRole = null;
        if(principal != null){
            memberRole = memberServiceImpl.getUser(principal.getName()).getRole();
        }
        logger.info(memberRole);
        if(principal == null || !memberRole.equalsIgnoreCase("admin")){
            model.addAttribute("memberRole", "notAdmin");
            noticePage = noticeService.search(searchCategory, keyword, pageNo, listSize);
        } else {
            model.addAttribute("memberRole", memberRole); //ADMIN
            noticePage = noticeService.searchForAdmin(noticeType, status, searchCategory, keyword, pageNo, listSize);
        }

        int endPage = (int)(Math.ceil((pageNo+1)/5.0))*5; //5의 배수
        int startPage = endPage - 4; //1, 5의 배수 +1 ...
        if(endPage > noticePage.getTotalPages()){
            int realEnd = noticePage.getTotalPages();
            model.addAttribute("endPage", realEnd);
            logger.info("realEnd: "+realEnd);
        } else {
            model.addAttribute("endPage", endPage);
        }
        logger.info("endPage: "+endPage);
        logger.info("startPage: "+startPage);


        model.addAttribute("noticeType", noticeType);
        model.addAttribute("status", status);
        model.addAttribute("listSize", listSize); //고정 완료
        model.addAttribute("searchCategory", searchCategory);
        model.addAttribute("keyword", keyword); //고정 완료
        model.addAttribute("noticePage", noticePage);
        model.addAttribute("startPage", startPage);
        return "notice/noticeList";

    }

    // 상세조회 (+댓글 목록조회 및 작성 폼)
    @GetMapping("/detail/{noticeNo}")
    public String getNoticeDetail(@PathVariable("noticeNo")Integer noticeNo,
                                  NoticeReplyForm noticeReplyForm,
                                  Principal principal, Model model){

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

        //번호로 공지사항데이터 가져오기
        Notice notice = noticeService.getNotice(noticeNo); //공지상세조회
        //조회수 증가 - 쿠키 이용해서 어뷰징 방지 (부가구현)
        noticeService.readCntUp(noticeNo);
        List<NoticeFiles> filesList=filesService.getFiles(notice); //파일조회
        List<NoticeReply> noticeReply = noticeService.getNoticeReply(notice); //댓글조회
        model.addAttribute("notice", notice); //공지상세 내용
        //model.addAttribute("noticeReply", noticeReply); //공지 댓글 목록
        model.addAttribute("filesList", filesList); //공지 파일 목록
        return "notice/noticeDetail";
    }

    //공지사항 삭제해줘 요청
    @GetMapping("/delete/{noticeNo}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String delete(@PathVariable Integer noticeNo, Principal principal, RedirectAttributes redirectAttributes){
        //우선 삭제할 글 조회
        Notice notice = noticeService.getNotice(noticeNo);
        Member member = memberServiceImpl.getUser(principal.getName());
        if(!notice.getMember().equals(member)){
            redirectAttributes.addFlashAttribute("msg","해당 게시글을 삭제할 권한이 없습니다.");
            return String.format("redirect:/notice/detail/%d", noticeNo);
        }
        List<NoticeFiles> filesList = filesService.getFiles(notice);
        noticeService.delete(notice); //DB에서 cascade되어 댓글과 파일데이터는 지워짐.

        //물리적 파일 삭제
        for(NoticeFiles file : filesList){
            filesService.deleteOnDisk(file); //디스크에서 삭제처리
        }
        return "redirect:/notice/list";
    }

    //공지 작성폼 보여줘 요청
    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String noticeAddForm(NoticeForm noticeForm, Model model, Principal principal){
        String memberRole = memberServiceImpl.getUser(principal.getName()).getRole();
        model.addAttribute("memberRole", memberRole);

        return "notice/noticeForm";
    }

    //공지사항 등록 처리해줘 요청
    @PostMapping("/add") //관리자만 작성 가능해야 함.
    @PreAuthorize("hasAuthority('ADMIN')")
    public String add(@Valid NoticeForm noticeForm, BindingResult bindingResult,
                      @RequestParam("uploadFiles") List<MultipartFile> uploadFiles,
                      Principal principal, RedirectAttributes redirectAttributes, Model model ){

        if(bindingResult.hasErrors()){ //에러가 있으면,
            logger.info("Errors: " +bindingResult);
            return "notice/noticeForm"; //noticeForm.html로 이동.
        }//에러가 없으면, 공지사항 등록 진행

        Member member = memberServiceImpl.getUser(principal.getName());
        model.addAttribute("memberRole", member.getRole());

        LocalDateTime today = LocalDateTime.now();
        //문자열로 받은 postDate를 LocalDateTime으로 변환.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime fPostDate = LocalDate.parse(noticeForm.getPostDate(), formatter).atStartOfDay();

        NoticeDTO noticeDTO = new NoticeDTO();
        noticeDTO.setNoticeType(noticeForm.getNoticeType());
        noticeDTO.setNoticeTitle(noticeForm.getTitle());
        noticeDTO.setNoticeContent(noticeForm.getContent());
        noticeDTO.setMember(member); //로그인한 유저 정보(수정 요)
        noticeDTO.setImp(noticeForm.getIsImp());

        if (noticeForm.getStatus().equals("SCHEDULED")){ //예약게시인 경우,
            noticeDTO.setStatus("not_post");
            noticeDTO.setWriteDate(today);
            noticeDTO.setPostDate(fPostDate);
        } else {//등록즉시 게시인경우
            noticeDTO.setStatus("post");
            noticeDTO.setWriteDate(today);
            noticeDTO.setPostDate(fPostDate);
        }
        //Notice 테이블에 저장
        Notice notice = noticeService.add(noticeDTO);

        //파일업로드(물리적 폴더에 저장)
        String uploadFolder = "C:/upload";
        List<NoticeFileDTO> list = new ArrayList<>();

        //파일업로드처리(DB NoticeFiles 테이블에 저장)
        for(MultipartFile multipartFile : uploadFiles) {
            if (!multipartFile.isEmpty()) {
                if (!filesService.fileSizeOk(multipartFile) || !filesService.fileTypeOk(multipartFile)) {
                    model.addAttribute("msg","파일을 업로드할 수 없습니다. 파일 확장자와 사이즈를 확인하세요.");
                    return "notice/noticeForm"; //noticeForm.html로 이동.
                }
                filesService.fileUpload(uploadFolder, notice, multipartFile, list, redirectAttributes);
            }
        }//파일 없으면,
        filesService.addFiles(list);

        redirectAttributes.addFlashAttribute("msg", "공지가 등록되었습니다.");
        return String.format("redirect:/notice/detail/%d", notice.getNoticeNo()); //상세조회로 이동
    }


    //공지사항 수정폼 보여줘 요청
    @GetMapping("/modify/{noticeNo}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String noticeModifyForm(@PathVariable("noticeNo")Integer noticeNo, NoticeForm noticeForm,
                                   Principal principal, Model model, RedirectAttributes redirectAttributes){
        //수정하려는 사람이 작성한 사람인지 확인.~~~~~~~~~~~
        Member member = memberServiceImpl.getUser(principal.getName());
        //공지사항 조회
        Notice notice = noticeService.getNotice(noticeNo);
        if(!notice.getMember().equals(member)){
            redirectAttributes.addFlashAttribute("msg","해당 게시글을 수정할 권한이 없습니다.");
            return String.format("redirect:/notice/detail/%d", noticeNo);
        }
        //파일 조회
        List<NoticeFiles> filesList=filesService.getFiles(notice);
        //조회된 공지사항의 내용을 수정폼에 세팅
        noticeForm.setNoticeType(notice.getNoticeType());
        noticeForm.setTitle(notice.getNoticeTitle());
        noticeForm.setContent(notice.getNoticeContent());
        if(notice.getStatus().equals("not_post")) {
            LocalDateTime postDate =notice.getPostDate();
            model.addAttribute("status", postDate);
        } else {
            model.addAttribute("status", "IMMEDIATE");
        }
        if(notice.getIsImp()){
            model.addAttribute("impCk", "impChecked");
        }
        
        model.addAttribute("memberRole", member.getRole()); //사이드바를 위한 모델설정
        model.addAttribute("filesList", filesList); //공지 파일 목록
        model.addAttribute("notice", notice); //공지 파일 목록
        
        return "notice/noticeModifyForm";
    }

    //공지사항 수정 처리해줘 요청
    @PostMapping("/modify/{noticeNo}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String modify(@PathVariable("noticeNo")Integer noticeNo,
                         @RequestParam("uploadFiles") List<MultipartFile> uploadFiles,
                         @Valid NoticeForm noticeForm, //유효성검사처리를 해주어야함
                         BindingResult bindingResult, //유효성검사의 결과
                         RedirectAttributes redirectAttributes,
                         Principal principal, Model model){

        String memberRole = memberServiceImpl.getUser(principal.getName()).getRole();
        model.addAttribute("memberRole", memberRole); //없어도 될듯?

        if(bindingResult.hasErrors()){ //에러가 있으면,
            logger.info("Errors: " +bindingResult);
            return "notice/noticeModifyForm"; //noticeModifyForm.html로 이동.
        }//에러가 없으면, 공지사항 등록 진행

        Notice notice = noticeService.getNotice(noticeNo);
        List<NoticeFiles> filesList=filesService.getFiles(notice); //파일조회

        NoticeDTO noticeDTO = new NoticeDTO();

        LocalDateTime today = LocalDateTime.now();
        //문자열로 받은 postDate를 LocalDateTime으로 변환.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime fPostDate = LocalDate.parse(noticeForm.getPostDate(), formatter).atStartOfDay();

        //DTO에 담기
        noticeDTO.setNoticeType(noticeForm.getNoticeType());
        noticeDTO.setNoticeTitle(noticeForm.getTitle());
        noticeDTO.setNoticeContent(noticeForm.getContent());
        noticeDTO.setImp(noticeForm.getIsImp());
        if (noticeForm.getStatus().equals("SCHEDULED")){ //예약게시인 경우,
            noticeDTO.setStatus("not_post");
            noticeDTO.setWriteDate(today);
            noticeDTO.setPostDate(fPostDate);
        } else {
            noticeDTO.setStatus("post");
            noticeDTO.setWriteDate(today);
            noticeDTO.setPostDate(fPostDate);
        }

        //Notice 테이블에 저장
        noticeService.modify(notice, noticeDTO);

        List<NoticeFileDTO> list = new ArrayList<>();

        //파일업로드(물리적 폴더에 저장)
        String uploadFolder = "C:/upload";
        //파일업로드처리(DB NoticeFiles 테이블에 저장)
        for(MultipartFile multipartFile : uploadFiles) {
            if (!multipartFile.isEmpty()) {
                if (!filesService.fileSizeOk(multipartFile) || !filesService.fileTypeOk(multipartFile)) {
                    model.addAttribute("msg","파일을 업로드할 수 없습니다. 파일 확장자와 사이즈를 확인하세요.");
                    if(notice.getStatus().equals("not_post")) {
                        LocalDateTime postDate =notice.getPostDate();
                        model.addAttribute("status", postDate);
                    } else {
                        model.addAttribute("status", "IMMEDIATE");
                    }
                    if(notice.getIsImp()){
                        model.addAttribute("impCk", "impChecked");
                    }
                    model.addAttribute("notice", notice);
                    model.addAttribute("filesList", filesList); //공지 파일 목록
                    return "notice/noticeModifyForm"; //noticeModifyForm.html로 이동.
                }
                filesService.fileUpload(uploadFolder, notice, multipartFile, list, redirectAttributes);
            }
        }//파일 없으면,
        filesService.addFiles(list);

        redirectAttributes.addFlashAttribute("msg", "공지가 수정되었습니다.");
        return String.format("redirect:/notice/detail/%d", noticeNo);
    }

    //자주묻는질문 목록
    //관리자 아닌 사람들이 주로 봄 (관리자도 볼 수 있음)
    @GetMapping("/faq")
    public String getFaqList(Model model, Pageable pageable,
                             @RequestParam(value = "page", defaultValue = "0") int pageNo,
                             @RequestParam(value = "listSize", defaultValue = "5") int listSize){
        logger.info("page: "+pageNo);
        Page<Notice> faqPage = this.noticeService.getFaqList(pageNo, listSize);
        int endPage = (int)(Math.ceil((pageNo+1)/5.0))*5; //5의 배수
        int startPage = endPage - 4; //1, 5의 배수 +1 ...
        if(endPage > faqPage.getTotalPages()){
            int realEnd = faqPage.getTotalPages();
            model.addAttribute("endPage", realEnd);
            logger.info("realEnd: "+realEnd);
        } else {
            model.addAttribute("endPage", endPage);
        }
        logger.info("endPage: "+endPage);
        logger.info("startPage: "+startPage);

        model.addAttribute("noticePage", faqPage);
        model.addAttribute("startPage", startPage);
        return "notice/faqList";
    }

    //FAQ검색
    @GetMapping("/faq/search")
    public String getFaqSearchList(@RequestParam(value = "page", defaultValue = "0") int pageNo,
                                   @RequestParam(value = "listSize", defaultValue = "5") int listSize,
                                   Model model, String keyword, Pageable pageable){

        Page<Notice> noticePage = noticeService.searchFaq(keyword, pageNo, listSize);
        int endPage = (int)(Math.ceil((pageNo+1)/5.0))*5; //5의 배수
        int startPage = endPage - 4; //1, 5의 배수 +1 ...
        if(endPage > noticePage.getTotalPages()){
            int realEnd = noticePage.getTotalPages();
            model.addAttribute("endPage", realEnd);
        } else {
            model.addAttribute("endPage", endPage);
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("noticePage", noticePage);
        model.addAttribute("startPage", startPage);
        return "notice/faqList";
    }


}
