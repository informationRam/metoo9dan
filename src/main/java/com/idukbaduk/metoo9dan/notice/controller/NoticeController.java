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
    public String getNoticeDetail(@PathVariable("noticeNo")Integer noticeNo, NoticeReplyForm noticeReplyForm,
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
        model.addAttribute("noticeReply", noticeReply); //공지 댓글 목록
        model.addAttribute("filesList", filesList); //공지 파일 목록
        return "notice/noticeDetail";
    }

    //공지사항 삭제해줘 요청
    @GetMapping("/delete/{noticeNo}")
    public String delete(@PathVariable Integer noticeNo){
        //우선 삭제할 글 조회
        Notice notice = noticeService.getNotice(noticeNo);
        List<NoticeFiles> filesList = filesService.getFiles(notice);
        //댓글 작성자와 로그인한 유저가 같지 않는 경우 BAD_REQUEST 응답처리하는 코드 추가해야함~~~~~~~~~
        /*if(member.~~~.equals(principal.getName())){
        }*/
        noticeService.delete(notice); //DB에서 cascade되어 댓글과 파일데이터는 지워짐.

        //물리적 파일 삭제
        for(NoticeFiles file : filesList){
            filesService.deleteOnDisk(file); //디스크에서 삭제처리
        }
        return "redirect:/notice/list";
    }

    //공지 작성폼 보여줘 요청
    @GetMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String noticeAddForm(NoticeForm noticeForm, Model model, Principal principal){
        String memberRole = memberServiceImpl.getUser(principal.getName()).getRole();
        //~~~ admin 아니면 폼 보여주면 안됨~~~~~~~~~~~~~~~~~~~~~
        model.addAttribute("memberRole", memberRole);

        return "notice/noticeForm";
    }

    //공지사항 등록 처리해줘 요청
    @PostMapping("/add") //관리자만 작성 가능해야 함.
    public String add(@Valid NoticeForm noticeForm, BindingResult bindingResult,
                      @RequestParam("uploadFiles") List<MultipartFile> uploadFiles,
                      Principal principal, RedirectAttributes redirectAttributes, Model model ){

        if(bindingResult.hasErrors()){ //에러가 있으면,
            logger.info("Errors: " +bindingResult);
            return "/notice/noticeForm"; //noticeForm.html로 이동.
        }//에러가 없으면, 공지사항 등록 진행

        //로그인한 사람이 관리자인지 확인하는 코드 필요.~~~~~~~~~~~~~~~~
        Member member = memberServiceImpl.getUser(principal.getName());
        String memberRole = memberServiceImpl.getUser(principal.getName()).getRole();
        model.addAttribute("memberRole", memberRole);

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
                if (!fileSizeOk(multipartFile) || !fileTypeOk(multipartFile)) {
                    model.addAttribute("msg","파일을 업로드할 수 없습니다. 파일 확장자와 사이즈를 확인하세요.");
                    return "notice/noticeForm"; //noticeForm.html로 이동.
                }
            }
            fileUpload(uploadFolder, notice, multipartFile, list, redirectAttributes);
        }//파일 없으면,
        filesService.addFiles(list);

        redirectAttributes.addFlashAttribute("msg", "공지가 등록되었습니다.");
        return "redirect:/notice/list"; //질문목록조회 요청
    }

    private boolean fileSizeOk(MultipartFile multipartFile){
        int maxSize = 31457280; //30MB
        logger.info("fileSize: " +multipartFile.getSize());
        if(multipartFile.getSize() > maxSize){
            return false;
        }
        return true;
    }
    private boolean fileTypeOk(MultipartFile multipartFile){
        if(!multipartFile.getContentType().contains("image")){
            return false;
        }
        return true;
    }

    //공지사항 수정폼 보여줘 요청
    @GetMapping("/modify/{noticeNo}")
    @PreAuthorize("isAuthenticated()")
    public String noticeModifyForm(@PathVariable("noticeNo")Integer noticeNo, NoticeForm noticeForm,
                                   Principal principal, Model model){
        //수정하려는 사람이 작성한 사람인지 확인.~~~~~~~~~~~
        String memberRole = memberServiceImpl.getUser(principal.getName()).getRole();
        //공지사항 조회
        Notice notice = noticeService.getNotice(noticeNo);
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
        
        model.addAttribute("memberRole", memberRole); //사이드바를 위한 모델설정
        model.addAttribute("filesList", filesList); //공지 파일 목록
        model.addAttribute("notice", notice); //공지 파일 목록
        
        return "notice/noticeModifyForm";
    }

    //공지사항 수정 처리해줘 요청
    @PostMapping("/modify/{noticeNo}")
    public String modify(@PathVariable("noticeNo")Integer noticeNo,
                         @RequestParam("uploadFiles") List<MultipartFile> uploadFiles,
                         @Valid NoticeForm noticeForm, //유효성검사처리를 해주어야함
                         BindingResult bindingResult, //유효성검사의 결과
                         RedirectAttributes redirectAttributes,
                         Principal principal, Model model){

        String memberRole = memberServiceImpl.getUser(principal.getName()).getRole();
        model.addAttribute("memberRole", memberRole);

        if(bindingResult.hasErrors()){ //에러가 있으면,
            logger.info("Errors: " +bindingResult);
            return "notice/noticeModifyForm"; //noticeModifyForm.html로 이동.
        }//에러가 없으면, 공지사항 등록 진행

        Notice notice = noticeService.getNotice(noticeNo);
        List<NoticeFiles> filesList=filesService.getFiles(notice); //파일조회

        //수정하려는 자와 작성자가 같은 사람인지 확인해야함~~~~~~~~~

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
                if (!fileSizeOk(multipartFile) || !fileTypeOk(multipartFile)) {
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
                fileUpload(uploadFolder, notice, multipartFile, list, redirectAttributes);
            }
        }//파일 없으면,
        filesService.addFiles(list);

        redirectAttributes.addFlashAttribute("msg", "공지가 수정되었습니다.");
        return String.format("redirect:/notice/detail/%d", noticeNo);
    }

    //자주묻는질문 목록
    @GetMapping("/faq")
    public String getFaqList(Model model, Pageable pageable,
                             @RequestParam(value = "page", defaultValue = "0") int pageNo,
                             @RequestParam(value = "listSize", defaultValue = "5") int listSize){
        logger.info("page: "+pageNo);
        Page<Notice> noticePage = this.noticeService.getFaqList(pageNo, listSize);
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

        model.addAttribute("noticePage", noticePage);
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

    //파일 업로드를 처리하는 메소드 (물리적 폴더와 파일 생성 및 DB에 저장)
    private void fileUpload(String uploadFolder, Notice notice, MultipartFile multipartFile, List<NoticeFileDTO> list, RedirectAttributes redirectAttributes) {
        // getFolder(): 년/월/일 폴더 생성
        String uploadFolderPath = getFolder();
        File uploadPath = new File(uploadFolder, uploadFolderPath);
        if(uploadPath.exists() == false){
            uploadPath.mkdirs();
        }

        //DTO에 담아 간다
        NoticeFileDTO fileDTO = new NoticeFileDTO(); //DTO객체 생성
        fileDTO.setNotice(notice); //방금 생성한 공지번호 저장

        String uploadFileName = multipartFile.getOriginalFilename();
        fileDTO.setOriginFileName(uploadFileName); //원본파일명 저장

        //파일이름 중복방지를 위한 UUID
        UUID uuid =UUID.randomUUID();
        uploadFileName = uuid.toString()+"_"+uploadFileName;

        try {
            File saveFile = new File(uploadPath, uploadFileName);
            multipartFile.transferTo(saveFile);
            fileDTO.setUuid(uuid.toString()); //사본파일명 저장
            fileDTO.setUploadPath(uploadFolderPath+""); //C:/upload이하 파일경로 저장
            list.add(fileDTO);
        } catch (IOException e) {
            logger.error(e.getMessage());
            redirectAttributes.addFlashAttribute("msg", "공지등록하는 중에 에러 발생");
        }//end catch
    }

    //중복된 이름의 파일처리
    //년/월/일 폴더 생성
    private String getFolder(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String str = sdf.format(date);
        return str.replace("-", "/");
    }

}
