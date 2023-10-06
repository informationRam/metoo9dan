package com.idukbaduk.metoo9dan.education.service;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import com.idukbaduk.metoo9dan.education.reprository.EducationRepository;
import com.idukbaduk.metoo9dan.education.reprository.ResourcesFilesReprository;
import com.idukbaduk.metoo9dan.education.vaildation.EducationVaildation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationalRepository;
    private final ResourcesFilesReprository resourcesFilesReprository;
    private final ResourcesFilesService resourcesFilesService;

    //교육자료등록 (파일같이저장)
    public void save(EducationVaildation educationVaildation) throws IOException {
        EducationalResources educationalResources = new EducationalResources();

        //파일이 있다면
        if(educationVaildation.getBoardFile() != null){

            //교육자료 내용 저장
            educationalResources.setResourceNo(educationVaildation.getResource_no());
            educationalResources.setResourceName(educationVaildation.getResource_name());
            educationalResources.setResourceCate(educationVaildation.getResource_cate());
            educationalResources.setFileType(educationVaildation.getFile_type());
            educationalResources.setServiceType(educationVaildation.getService_type());
            educationalResources.setDescription(educationVaildation.getDescription());
            educationalResources.setCreationDate(LocalDateTime.now());
            educationalResources.setGameContents(null);
            educationalResources.setFileUrl(null);
            educationalRepository.save(educationalResources);   //교육자료를 저장

            EducationalResources educationalResources1 = educationalRepository.findById(educationalResources.getResourceNo()).get();
            for(MultipartFile boardFile:educationVaildation.getBoardFile()){
                ResourcesFiles resourcesFiles = new ResourcesFiles(); // 이미지 저장을 위한 객체 생성
                resourcesFiles.setEducationalResources(educationalResources);
                String originalFileName = boardFile.getOriginalFilename(); //파일이름을 가져옴
                String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String copyFileName = todayDate + "_" + originalFileName;    //파일저장명 'yyyyMMddHHmmss+원본파일명'
                String savePath = "/Users/ryuahn/Desktop/baduk/education/"+copyFileName; //mac 파일 지정 C:/baduk
                boardFile.transferTo(new File(savePath));   //파일저장 처리

                resourcesFiles.setOriginFileName(originalFileName);
                resourcesFiles.setCopyFileName(copyFileName);
                resourcesFilesReprository.save(resourcesFiles);
            }

        }else {
            //교육자료 내용 저장
            educationalResources.setResourceName(educationVaildation.getResource_name());
            educationalResources.setResourceCate(educationVaildation.getResource_cate());
            educationalResources.setFileType(educationVaildation.getFile_type());
            educationalResources.setFileUrl(null);
            educationalResources.setServiceType(educationVaildation.getService_type());
            educationalResources.setDescription(educationVaildation.getDescription());
            educationalResources.setCreationDate(LocalDateTime.now());

            //교육자료 저장
            educationalRepository.save(educationalResources);
        }
    }
    //교육자료목록조회 (페이징처리)
    public Page<EducationalResources> getList(int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("creationDate"));     //등록일순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return educationalRepository.findAll(pageable);
    }

    //교육자료수정
    public void modify(EducationalResources educationalResources,EducationVaildation educationVaildation) throws IOException {

        // 필요한 필드를 업데이트
        educationalResources.setResourceName(educationVaildation.getResource_name());
        educationalResources.setResourceCate(educationVaildation.getResource_cate());
        educationalResources.setFileType(educationVaildation.getFile_type());
        educationalResources.setServiceType(educationVaildation.getService_type());
        educationalResources.setDescription(educationVaildation.getDescription());
        educationalResources.setCreationDate(LocalDateTime.now());

        // 수정된 교육 자료 저장
        educationalRepository.save(educationalResources);

        List<MultipartFile> boardFile1 = educationVaildation.getBoardFile();
        System.out.println("boardFile1 " + boardFile1);
        if (boardFile1 != null && !boardFile1.isEmpty()) {
            for (MultipartFile boardFile : boardFile1) {
                if (boardFile != null && !boardFile.isEmpty()) { // 파일이 비어있지 않은 경우에만 저장 로직 실행
                    System.out.println("boardFile?" + boardFile.getOriginalFilename());
                    // 나머지 저장 로직은 그대로 유지
                    ResourcesFiles resourcesFiles = new ResourcesFiles();
                    resourcesFiles.setEducationalResources(educationalResources);
                    String originalFileName = boardFile.getOriginalFilename();
                    String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                    String copyFileName = todayDate + "_" + originalFileName;
                    String savePath = "/Users/ryuahn/Desktop/baduk/education/" + copyFileName;
                    boardFile.transferTo(new File(savePath));
                    resourcesFiles.setOriginFileName(originalFileName);
                    resourcesFiles.setCopyFileName(copyFileName);
                    resourcesFilesReprository.save(resourcesFiles);
                }
            }
        }
    }

    //resourceNo 주면 Contents값 전체 조회
    public EducationalResources getEducation(int resourceNo){
        EducationalResources educationalResources = educationalRepository.findById(resourceNo).orElse(null);
        if (educationalResources != null) {
            educationalResources.setResourcesFilesList(resourcesFilesService.getResourcesFilesByResourceNo(resourceNo));
        }
        return educationalResources;
    }

    //EducationalResources -> EducationVaildation으로 변경
    public EducationVaildation toEducationVaildation(EducationalResources education) {
        EducationVaildation educationVaildation = new EducationVaildation();
        educationVaildation.setResource_no(education.getResourceNo());
        educationVaildation.setResource_name(education.getResourceName());
        educationVaildation.setResource_cate(education.getResourceCate());
        educationVaildation.setFile_type(education.getFileType());
        educationVaildation.setFile_url(education.getFileUrl());
        educationVaildation.setService_type(education.getServiceType());
        educationVaildation.setDescription(education.getDescription());
        // 이미 업로드된 이미지 파일 목록을 가져와서 EducationVaildation에 설정
        List<ResourcesFiles> resourcesFilesList = resourcesFilesService.getResourcesFilesByResourceNo(education.getResourceNo());
        educationVaildation.setBoardFileList(resourcesFilesList);

        return educationVaildation;
    }

    //  EducationVaildation -> EducationalResources 변경
    public EducationVaildation toEducationalResources(EducationVaildation educationVaildation) {
        EducationalResources educationalResources = new EducationalResources();

        educationalResources.setResourceName(educationVaildation.getResource_name());
        educationalResources.setResourceCate(educationVaildation.getResource_cate());
        educationalResources.setFileType(educationVaildation.getFile_type());
        educationalResources.setFileUrl(null);
        educationalResources.setServiceType(educationVaildation.getService_type());
        educationalResources.setDescription(educationVaildation.getDescription());
        educationalResources.setCreationDate(LocalDateTime.now());

        // 이미 업로드된 이미지 파일 목록을 가져와서 EducationVaildation에 설정
        List<ResourcesFiles> resourcesFilesList = resourcesFilesService.getResourcesFilesByResourceNo(educationVaildation.getResource_no());
        educationVaildation.setBoardFileList(resourcesFilesList);

        return educationVaildation;
    }

// 삭제 처리
    public void delete(EducationalResources education) {
        List<ResourcesFiles> byEducationalResourcesResourceNo = resourcesFilesReprository.findByEducationalResources_ResourceNo(education.getResourceNo());
        educationalRepository.delete(education);

        //EducationalResources와 연관된 파일이 있다면 삭제.
        if(byEducationalResourcesResourceNo != null && !byEducationalResourcesResourceNo.isEmpty()) {
            for (ResourcesFiles resourcesFiles : byEducationalResourcesResourceNo) {
                resourcesFilesService.deleteFile(resourcesFiles.getFileNo());
                //resourcesFilesReprository.delete(resourcesFiles);
            }
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

}//class

