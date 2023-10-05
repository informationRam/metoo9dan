package com.idukbaduk.metoo9dan.education.controller;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import com.idukbaduk.metoo9dan.education.service.EducationService;
import com.idukbaduk.metoo9dan.education.service.ResourcesFilesService;
import com.idukbaduk.metoo9dan.education.vaildation.EducationVaildation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    private final ResourcesFilesService resourcesFilesService;

    //교육자료 등록 폼
    @GetMapping("/addForm")
    public String educationAddForm(EducationVaildation educationVaildation, Model model) {
        model.addAttribute("educationVaildation", educationVaildation);
        return "education/addForm";
    }

    //교육자료 등록 처리
    @PostMapping("/add")
    public String educationAdd(@ModelAttribute("educationVaildation") @Valid EducationVaildation educationVaildation, @RequestParam("boardFile") MultipartFile file, BindingResult bindingResult, Model model) throws IOException {
        System.out.println("boardFile?"+educationVaildation.getBoardFile().get(0));

        // 업로드된 파일의 확장자 확인
        MultipartFile fileName = educationVaildation.getBoardFile().get(0);

        if (fileName == null || fileName.isEmpty()) {
            educationVaildation.setBoardFile(null);
        }

        if (bindingResult.hasErrors()) {
            return "education/addForm";
        } else{
            educationService.save(educationVaildation);
            return "redirect:/education/list";
        }
    }

    //교육자료 목록조회
    @GetMapping("/list")
    public String educationList(Model model, @RequestParam(value = "page", defaultValue = "0") int page, EducationalResources educationalResources) {
        Page<EducationalResources> educationPage = this.educationService.getList(page);

        // 교육자료에 대한 파일 정보를 가져와서 모델에 추가
        for (EducationalResources education : educationPage.getContent()) {
            List<ResourcesFiles> resourcesFilesList = resourcesFilesService.getResourcesFilesByResourceNo(education.getResourceNo());
            education.setResourcesFilesList(resourcesFilesList);
        }

        //3.Model
        model.addAttribute("educationPage", educationPage);
        model.addAttribute("educationalResources", educationalResources);
        return "education/list";
    }

    //수정하기
    @GetMapping("/modify/{resourceNo}")
    public String userUpdateForm(@PathVariable Integer resourceNo, Model model) {
        EducationalResources education = educationService.getEducation(resourceNo);
        EducationVaildation educationVaildation = educationService.toEducationVaildation(education);// 인증을위한 userModifyForm값으로 변경

        model.addAttribute("educationVaildation", educationVaildation);
        return "education/modify";

    }

    // 파일 다운로드 요청 처리
    @GetMapping("/downloadFile/{fileNo}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer fileNo) {
        // 파일을 서버에서 가져오는 로직을 구현하고 Resource 객체를 생성
        ResourcesFiles resourcesFile = resourcesFilesService.getFileByFileNo(fileNo);
        if (resourcesFile == null) {
            // 파일이 존재하지 않으면 에러 응답을 보낼 수 있습니다.
            return ResponseEntity.notFound().build();
        }

        // 파일 경로 가져오기
        String filePath = "/Users/ryuahn/Desktop/baduk/education/" + resourcesFile.getCopyFileName(); // 파일이 저장된 경로

        System.out.println("resourcesFile.getCopyFileName()?: "+resourcesFile.getCopyFileName());
        try {
            // 파일을 바이트 배열로 읽기
            byte[] fileData = Files.readAllBytes(new File(filePath).toPath());

            // 파일 다운로드를 위한 HTTP 헤더 설정
            // 파일 다운로드를 위한 HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();

            // 파일 이름을 UTF-8로 인코딩하여 설정
            String fileName = resourcesFile.getCopyFileName();
            String encodedFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            System.out.println("encodedFileName?: " +encodedFileName);
            headers.setContentDispositionFormData("attachment", encodedFileName);

            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // 파일 데이터를 ByteArrayResource로 래핑하여 응답으로 보냅니다.
            ByteArrayResource resource = new ByteArrayResource(fileData);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(fileData.length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            // 파일을 읽을 수 없는 경우 에러 응답을 보냅니다.
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
}

    // 파일 삭제 핸들러
    @GetMapping("/deleteFile/{fileNo}")
    public String deleteFile(@PathVariable Integer fileNo) {
        // 파일을 서버에서 삭제하는 로직을 구현
        ResourcesFiles resourcesFile = resourcesFilesService.getFileByFileNo(fileNo);
        if (resourcesFile != null) {
            // 파일 삭제 로직을 구현 (예: 파일 시스템에서 삭제)
            String filePath = "/Users/ryuahn/Desktop/baduk/education/" + resourcesFile.getCopyFileName();
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                file.delete(); // 파일을 삭제
            }

            // 데이터베이스에서 파일 정보를 삭제
            resourcesFilesService.deleteFile(fileNo);
        }

        return "redirect:/education/addForm"; // 파일 삭제 후 다시 등록 페이지로 리디렉션
    }

    // 정보수정 실행
 /*   @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{user_id}")
    public String userUpdate(Model model, @Valid UserModifyForm userModifyForm,
                             BindingResult bindingResult, Principal principal) {

        User user = userService.getUser(principal.getName());
        model.addAttribute("userModifyForm", userModifyForm);

        if (bindingResult.hasErrors()) {
            return "user/userModifyForm";
        }
        //이메일 중복여부체크
        if (userService.checkEmailDuplication(user, userModifyForm)) {
            bindingResult.rejectValue("email", "EmailInCorrect", "이미 사용중인 이메일 입니다.");
            return "user/userModifyForm";
        }
        //비밀번호, 비밀번호 확인 동일 체크
        if (!userModifyForm.getPwd1().equals(userModifyForm.getPwd2())) {
            bindingResult.rejectValue("pwd2", "pwdInCorrect", "비밀번호와 비밀번호확인이 불일치합니다.");
            return "user/userModifyForm";
        }
        try {
            userService.userModify(user, userModifyForm);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("modifyFailed", "정보를 확인해주세요.");
            return "user/userModifyForm";
        }
        return "redirect:/";
    }
*/



}
