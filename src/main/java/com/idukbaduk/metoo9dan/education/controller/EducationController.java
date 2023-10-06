package com.idukbaduk.metoo9dan.education.controller;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
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
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    //수정하기 폼
    @GetMapping("/modify/{resourceNo}")
    public String userUpdateForm(@PathVariable Integer resourceNo, Model model) {
        EducationalResources education = educationService.getEducation(resourceNo);
        EducationVaildation educationVaildation = educationService.toEducationVaildation(education);// 인증을위한 userModifyForm값으로 변경

        model.addAttribute("educationVaildation", educationVaildation);
        return "education/modify";

    }

    //수정처리
    @PostMapping("/modify/{resourceNo}")
    public String modifyResource(@PathVariable Integer resourceNo, @ModelAttribute("educationVaildation") EducationVaildation educationVaildation, @RequestParam(name = "deletedFiles", required = false) List<Integer> deletedFiles, @RequestParam(name = "modifiedContent", required = false) String modifiedContent) throws IOException {
        // 1. 삭제된 파일 처리
        if (deletedFiles != null && !deletedFiles.isEmpty()) {
            System.out.println("deletedFiles?: "+ deletedFiles);
            for (Integer fileNo : deletedFiles) {
                if(fileNo != null && !deletedFiles.isEmpty()){
                    System.out.println("fileNo?: "+ fileNo);
                    // 파일을 서버에서 삭제하는 로직을 구현
                    ResourcesFiles resourcesFile = resourcesFilesService.getFileByFileNo(fileNo);
                    // 파일 삭제 로직을 구현 (예: 파일 시스템에서 삭제)
                    String filePath = "/Users/ryuahn/Desktop/baduk/education/" + resourcesFile.getCopyFileName();
                    File file = new File(filePath);
                    if (file.exists() && file.isFile()) {
                        file.delete(); // 파일을 삭제
                    }
                    // 데이터베이스에서 파일 정보를 삭제
                    resourcesFilesService.deleteFile(fileNo);
                }
            }
        }
        // 2. 수정된 컨텐츠 내용 처리
            EducationalResources educationalResources = educationService.getEducation(resourceNo);
            if (educationalResources != null) {
                educationService.modify(educationalResources, educationVaildation);
                return "redirect:/education/list";
            }

        return "redirect:/education/list";
    }

    //삭제처리
    @GetMapping("/delete/{resourceNo}")
    public String delete(@PathVariable("resourceNo") Integer resourceNo, Principal principal) {
        EducationalResources education = educationService.getEducation(resourceNo);
        Integer gameContentNo = education.getGameContents().getGameContentNo();
        if (gameContentNo > 0){
            educationService.delete(education);
            return "redirect:/education/list";    // 공지사항 목록으로 이동
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
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

   /* // 파일 삭제 핸들러
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
    }*/

}
