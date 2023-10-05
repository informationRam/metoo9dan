package com.idukbaduk.metoo9dan.education.service;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import com.idukbaduk.metoo9dan.education.reprository.EducationRepository;
import com.idukbaduk.metoo9dan.education.reprository.ResourcesFilesReprository;
import com.idukbaduk.metoo9dan.education.vaildation.EducationVaildation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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

            //교육자료파일 저장
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
}
