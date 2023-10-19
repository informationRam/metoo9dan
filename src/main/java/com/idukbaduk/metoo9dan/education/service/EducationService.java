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

import java.io.IOException;
import java.time.LocalDateTime;
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

    //교육자료목록조회 ( 전체 페이징처리)
    public Page<EducationalResources> getList(int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("creationDate"));     //등록일순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return educationalRepository.findAll(pageable);
    }

    //교육자료목록조회 (교육자료로 검색 - 페이징처리)
    public Page<EducationalResources> getresourcecateList(String resourceCate, int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("creationDate"));     //등록일순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return educationalRepository.findByResourceCate(resourceCate,pageable);
    }


    //교육자료 제목으로 (교육자료 - 페이징처리)
    public Page<EducationalResources> getresourceName(String resourceName, int page) {
        System.out.println("resourceName?"+resourceName);
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("creationDate"));     //등록일순

        System.out.println("sorts?"+sorts);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        System.out.println("pageable?"+pageable);


        return educationalRepository.findByResourceNameContaining(resourceName,pageable);
    }


    //게임, 교육자료를 기준으로 페이지 네이션
    public Page<EducationalResources> getFilteredResources(Integer gameContentNo, String resourceCate,int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("creationDate"));     //등록일순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return educationalRepository.findByGameContentsGameContentNoAndResourceCateContaining(gameContentNo, resourceCate, pageable);
    }

    //게임을 기준으로 페이지 네이션
    public Page<EducationalResources> getFilterGame(Integer gameContentNo,int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("creationDate"));     //등록일순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return educationalRepository.findByGameContentsGameContentNo(gameContentNo, pageable);
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
            educationalResources.setResourcesFilesList(resourcesFilesService.getFile(resourceNo));
        }
        return educationalResources;
    }

    public List<EducationalResources> getEducationDistinct() {
        List<EducationalResources> uniqueResources = educationalRepository.findResourcesWithUniqueResourceNames();
        return uniqueResources;
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
        ResourcesFiles resourcesFilesList = resourcesFilesService.getFile(education.getResourceNo());

        educationValidation.setSaveThumFile(resourcesFilesList);
        educationValidation.setSaveboardFile(resourcesFilesList);

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

        ResourcesFiles byEducationalResourcesResourceNo = resourcesFilesReprository.findByEducationalResources_ResourceNo(education.getResourceNo());
        System.out.println("byEducationalResourcesResourceNo?:" + byEducationalResourcesResourceNo);

        if (byEducationalResourcesResourceNo != null && !byEducationalResourcesResourceNo.equals("")) {
                resourcesFilesService.alldeleteFile(byEducationalResourcesResourceNo); // 삭제 서비스로 전달
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
        ResourcesFiles ori_resourcesfiles = resourcesFilesService.getFile(ori_education.getResource_no());
        //파일이 있다면 (DB에 파일 정보만 저장처리한다.)
        if(!ori_resourcesfiles.equals(null) || !ori_resourcesfiles.equals("")){
                ResourcesFiles copyResourcesFiles = new ResourcesFiles(); // DB에 이미지 제목 복사처리
                copyResourcesFiles.setOriginFileName(ori_resourcesfiles.getOriginFileName());
                copyResourcesFiles.setCopyFileName(ori_resourcesfiles.getCopyFileName());
                copyResourcesFiles.setEducationalResources(copy_education);
                resourcesFilesReprository.save(copyResourcesFiles);
            }

    }

    // gameno값으로 EducationalResources 정보 가져오기
    public List<EducationalResources> getEducation_togameno(int gameContentNo) {
        List<EducationalResources> educationalResourcesList = educationalRepository.findByGameContents_GameContentNo(gameContentNo);

            return educationalResourcesList;
    }
}//class

