package com.idukbaduk.metoo9dan.notice.controller;

import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeFiles;
import com.idukbaduk.metoo9dan.common.entity.NoticeReply;
import com.idukbaduk.metoo9dan.member.service.MemberService;
import com.idukbaduk.metoo9dan.member.service.MemberServiceImpl;
import com.idukbaduk.metoo9dan.notice.dto.NoticeDTO;
import com.idukbaduk.metoo9dan.notice.dto.NoticeFileDTO;
import com.idukbaduk.metoo9dan.notice.service.NoticeFilesService;
import com.idukbaduk.metoo9dan.notice.service.NoticeService;
import com.idukbaduk.metoo9dan.notice.validation.NoticeForm;
import com.idukbaduk.metoo9dan.notice.validation.NoticeReplyForm;
import groovy.util.logging.Log4j2;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @Autowired
    private HttpSession httpSession;

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
    public String getNoticeDetail(@PathVariable("noticeNo")Integer noticeNo, NoticeReplyForm noticeReplyForm,
                                  //Principal principal,
                                  Model model){

        Notice notice = noticeService.getNotice(noticeNo);

        List<NoticeFiles> filesList=filesService.getFiles(notice);
        List<NoticeReply> noticeReply = noticeService.getNoticeReply(notice);
        model.addAttribute("notice", notice); //공지상세 내용
        model.addAttribute("noticeReply", noticeReply); //공지 댓글 목록
        model.addAttribute("filesList", filesList); //공지 파일 목록
        return "notice/noticeDetail";
    }

    //공지사항 삭제해줘 요청
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{noticeNo}")
    public String delete(@PathVariable Integer noticeNo,
                         Principal principal){
        Member member = memberServiceImpl.getUser(principal.getName());
        logger.info("member: "+member.toString());
        // 1. 파라미터 받기
        // 2. 비즈니스 로직 처리
        //우선 삭제할 글이 존재하는지 조회하여 확인
        Notice notice = noticeService.getNotice(noticeNo);
        //댓글 작성자와 로그인한 유저가 같지 않는 경우 BAD_REQUEST 응답처리하는 코드 추가해야함
        /*if(member.~~~.equals(principal.getName())){
        }*/
        noticeService.delete(notice);
        //물리적 파일도 삭제하는 로직 추가해야함

        // 3. 모델
        // 4. 뷰
        return "redirect:/notice/list";
    }

    //공지 작성폼 보여줘 요청
    @GetMapping("/add")
    public String noticeAddForm(NoticeForm noticeForm){
        return "notice/noticeForm";
    }

    //공지사항 등록 처리해줘 요청
    @PostMapping("/add") //관리자만 작성 가능해야 함.
    public String add(@Valid NoticeForm noticeForm,
                      BindingResult bindingResult,
                      @RequestParam("uploadFiles") List<MultipartFile> uploadFiles,
                      RedirectAttributes redirectAttributes ){
        if(bindingResult.hasErrors()){ //에러가 있으면,
            logger.info("Errors: " +bindingResult);
            return "/notice/noticeForm"; //noticeForm.html로 이동.

        }//에러가 없으면, 공지사항 등록 진행

        //1. 파라미터 받기
        //로그인한 사람이 관리자인지 확인하는 코드 필요.
        //임시로 작성자 memberNo1로 설정
        Member member = new Member();
        member.setMemberNo(1);

        //2. 비즈니스로직 수행
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

        List<NoticeFileDTO> list = new ArrayList<>();
        //파일업로드(물리적 폴더에 저장)
       /* String uploadFolder = "C:/upload";*/
        String uploadFolder = "/Users/ryuahn/Desktop/baduk/noti/";
        // getFolder(): 년/월/일 폴더 생성
        String uploadFolderPath = getFolder();
        File uploadPath = new File(uploadFolder, uploadFolderPath);

        logger.info("uploadPath: "+uploadPath); // C:/upload/yyyy/MM/dd

        if(uploadPath.exists() == false){
            uploadPath.mkdirs();
        }

        //파일업로드처리(DB NoticeFiles 테이블에 저장)
        //DTO에 담아 간다
        for(MultipartFile multipartFile : uploadFiles){
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
        }//end for
        noticeService.addFiles(list);

        //3. 모델
        redirectAttributes.addFlashAttribute("msg", "공지가 등록되었습니다.");
        //4. 뷰
        return "redirect:/notice/list"; //질문목록조회 요청
    }

    //중복된 이름의 파일처리
    //년/월/일 폴더 생성
    private String getFolder(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String str = sdf.format(date);
        return str.replace("-", File.separator);
    }

    //공지사항 수정폼 보여줘 요청
    @GetMapping("/modify/{noticeNo}")
    public String noticeModifyForm(@PathVariable("noticeNo")Integer noticeNo,
                                   NoticeForm noticeForm,
                                   //Principal principal,
                                   Model model){
        //1.파라미터받기
        //2.비즈니스로직수행
        Notice notice = noticeService.getNotice(noticeNo);
        List<NoticeFiles> filesList=filesService.getFiles(notice);

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
        
        //파일도 수정하기 위해 보여줘야함
        
        //3.모델
        model.addAttribute("filesList", filesList); //공지 파일 목록
        model.addAttribute("notice", notice); //공지 파일 목록
        //4.뷰
        return "notice/noticeModifyForm";
    }

    //공지사항 수정 처리해줘 요청
    @PostMapping("/modify/{noticeNo}")
    public String modify(@PathVariable("noticeNo")Integer noticeNo,
                         @Valid NoticeForm noticeForm, //유효성검사처리를 해주어야함
                         BindingResult bindingResult, //유효성검사의 결과
                         RedirectAttributes redirectAttributes){

        Notice notice = noticeService.getNotice(noticeNo);

        //수정하려는 자와 작성자가 같은 사람인지 확인해야함

        NoticeDTO noticeDTO = new NoticeDTO();

        LocalDateTime today = LocalDateTime.now();
        //문자열로 받은 postDate를 LocalDateTime으로 변환.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime fPostDate = LocalDate.parse(noticeForm.getPostDate(), formatter).atStartOfDay();

        //1.파라미터받기
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
        
        //파일도 수정처리해야함
        
        //2.비즈니스로직수행
        noticeService.modify(notice, noticeDTO);
        //3.모델
        //4.뷰
        return String.format("redirect:/notice/detail/%d", noticeNo);
    }
}
