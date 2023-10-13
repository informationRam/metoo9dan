package com.idukbaduk.metoo9dan.education.service;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import com.idukbaduk.metoo9dan.education.reprository.EducationRepository;
import com.idukbaduk.metoo9dan.education.reprository.ResourcesFilesReprository;
import com.idukbaduk.metoo9dan.education.vaildation.EducationValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public EducationalResources save(EducationValidation educationValidation) throws IOException {
        // 파일을 저장하고, 교육 자료를 저장하는 로직을 포함
        EducationalResources educationalResources = toEducationalResources(educationValidation);
        EducationalResources educationalResources1 = educationalRepository.save(educationalResources);   //교육자료를 저장
        return educationalResources1;

    }




    //교육자료목록조회 (페이징처리)
    public Page<EducationalResources> getList(int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("creationDate"));     //등록일순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return educationalRepository.findAll(pageable);
    }

    //교육자료목록조회
    public List<EducationalResources> getAllEducation() {
        return educationalRepository.findAll(); // 페이지네이션 없이 모든 EducationalResources을 가져옵니다.
    }

    //교육자료수정
    public EducationalResources modify(EducationalResources educationalResources,EducationValidation educationValidation) throws IOException {

        // 필요한 필드를 업데이트
        educationalResources.setResourceName(educationValidation.getResource_name());
        educationalResources.setResourceCate(educationValidation.getResource_cate());
        educationalResources.setFileType(educationValidation.getFile_type());
        educationalResources.setServiceType(educationValidation.getService_type());
        educationalResources.setDescription(educationValidation.getDescription());
        educationalResources.setCreationDate(LocalDateTime.now());

        // 수정된 교육 자료 저장
        EducationalResources saveEducationalResources = educationalRepository.save(educationalResources);
        return saveEducationalResources;
    }

    // 교육자료명으로 교육자료 찾기
    public List<EducationalResources> search(String searchKeyword) {
        List<EducationalResources> resources = educationalRepository.findByResourceNameContains(searchKeyword);
        // 값이 있을 경우
        if (!resources.isEmpty()) {
            return resources;
        } else {
            return null;
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
    public EducationValidation toEducationValidation(EducationalResources education) {
        EducationValidation educationValidation = new EducationValidation();
        educationValidation.setResource_no(education.getResourceNo());
        educationValidation.setResource_name(education.getResourceName());
        educationValidation.setResource_cate(education.getResourceCate());
        educationValidation.setFile_type(education.getFileType());
        educationValidation.setService_type(education.getServiceType());
        educationValidation.setDescription(education.getDescription());

        // 이미 업로드된 이미지 파일 목록을 가져와서 EducationVaildation에 설정
        List<ResourcesFiles> resourcesFilesList = resourcesFilesService.getResourcesFilesByResourceNo(education.getResourceNo());
        educationValidation.setBoardFileList(resourcesFilesList);

        for(ResourcesFiles file : resourcesFilesList){
            if(file != null){
                educationValidation.setSaveThumFile(file);
            }
        }
        return educationValidation;
    }

    //  EducationVaildation -> EducationalResources 변경
    public EducationalResources toEducationalResources(EducationValidation educationValidation) {
        EducationalResources educationalResources = new EducationalResources();

        educationalResources.setResourceName(educationValidation.getResource_name());
        educationalResources.setResourceCate(educationValidation.getResource_cate());
        educationalResources.setFileType(educationValidation.getFile_type());
        educationalResources.setServiceType(educationValidation.getService_type());
        educationalResources.setDescription(educationValidation.getDescription());
        educationalResources.setCreationDate(LocalDateTime.now());
        educationalResources.setStatus("Y");
        educationalResources.setGameContents(null);

        return educationalResources;
    }

// 삭제 처리
    public void delete(EducationalResources education) {
        educationalRepository.delete(education);

        List<ResourcesFiles> byEducationalResourcesResourceNo = resourcesFilesReprository.findByEducationalResources_ResourceNo(education.getResourceNo());
        System.out.println("byEducationalResourcesResourceNo?:" + byEducationalResourcesResourceNo);

        if (byEducationalResourcesResourceNo != null && !byEducationalResourcesResourceNo.isEmpty()) {
            List<Integer> deletedFiles = new ArrayList<>(); // 리스트를 초기화하고 선언
            for (ResourcesFiles deletedFileList : byEducationalResourcesResourceNo) {
                deletedFiles.add(deletedFileList.getFileNo()); // 파일 번호를 리스트에 추가
            }
            resourcesFilesService.deleteFile(deletedFiles); // 삭제 서비스로 전달
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

    //게임콘텐츠에서 패키지로 추가처리함
    public void pgInsert(EducationalResources educationalResources,GameContents gameContents) {
        educationalResources.setGameContents(gameContents);
        educationalRepository.save(educationalResources);
    }

    // 기존에 게임 콘텐츠를 가지고 있다면 기존내용을 복사해 새로 저장함.
    public void copysave(EducationValidation ori_education, GameContents gameContents) {
        System.out.println("값이 존재함..!gameContents? : " + gameContents);
        EducationalResources copy_education = new EducationalResources();   //복사할 객체 생성

        copy_education.setResourceName(ori_education.getResource_name());
        copy_education.setResourceCate(ori_education.getResource_cate());
        copy_education.setFileType(ori_education.getFile_type());
        copy_education.setServiceType(ori_education.getService_type());
        copy_education.setDescription(ori_education.getDescription());
        copy_education.setCreationDate(LocalDateTime.now());
        copy_education.setGameContents(gameContents);
        educationalRepository.save(copy_education);   //교육자료를 저장

        EducationalResources educationalResources1 = educationalRepository.findById(copy_education.getResourceNo()).get();
        List<ResourcesFiles> ori_resourcesfiles = resourcesFilesService.getResourcesFilesByResourceNo(ori_education.getResource_no());
        //파일이 있다면 (DB에 파일 정보만 저장처리한다.)
        if(!ori_resourcesfiles.equals(null) || !ori_resourcesfiles.isEmpty()){
            for(ResourcesFiles resourcesFiles : ori_resourcesfiles){
                ResourcesFiles copyResourcesFiles = new ResourcesFiles(); // DB에 이미지 제목 복사처리
                copyResourcesFiles.setOriginFileName(resourcesFiles.getOriginFileName());
                copyResourcesFiles.setCopyFileName(resourcesFiles.getCopyFileName());
                copyResourcesFiles.setEducationalResources(copy_education);
                resourcesFilesReprository.save(copyResourcesFiles);
            }
        }
    }
}//class

