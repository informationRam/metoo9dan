package com.idukbaduk.metoo9dan.education.service;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import com.idukbaduk.metoo9dan.education.reprository.ResourcesFilesReprository;
import com.idukbaduk.metoo9dan.education.vaildation.EducationValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional  //부모entity에 접근할때 필요함
public class ResourcesFilesService {

    private String FileLocation = "/Users/ryuahn/Desktop/baduk/education/";

    @Autowired
    private final ResourcesFilesReprository resourcesFilesRepository;

    @Autowired
    public ResourcesFilesService(ResourcesFilesReprository resourcesFilesRepository) {
        this.resourcesFilesRepository = resourcesFilesRepository;
    }

    public List<ResourcesFiles> getResourcesFilesByResourceNo(Integer resourceNo) {
        return resourcesFilesRepository.findByEducationalResources_ResourceNo(resourceNo);
    }

    public ResourcesFiles getFileByFileNo(Integer fileNo) {
        ResourcesFiles resourcesFiles = resourcesFilesRepository.findById(fileNo).get();
        return resourcesFiles;
    }

    //저장처리 (파일도 저장함)
    public void save(EducationalResources educationalResources, EducationValidation educationValidation,String fileUrl) throws IOException {
        for (MultipartFile boardFile : educationValidation.getBoardFile()) {
            ResourcesFiles resourcesFiles = new ResourcesFiles(); // 이미지 저장을 위한 객체 생성
            String originalFileName = boardFile.getOriginalFilename(); //파일이름을 가져옴
            String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String copyFileName = todayDate + "_" + originalFileName;    //파일저장명 'yyyyMMddHHmmss+원본파일명'
            String savePath = fileUrl + copyFileName;   //mac 파일 지정 C:/baduk
            boardFile.transferTo(new File(savePath));   //파일저장 처리

            resourcesFiles.setEducationalResources(educationalResources);
            resourcesFiles.setOriginFileName(originalFileName);
            resourcesFiles.setCopyFileName(copyFileName);
            resourcesFilesRepository.save(resourcesFiles);
        }
    }

    public void deleteFile(Integer fileNo) {
        ResourcesFiles resourcesFiles = resourcesFilesRepository.findById(fileNo).get();
        // 파일 삭제 로직을 구현 (예: 파일 시스템에서 삭제)

        String filePath = FileLocation + resourcesFiles.getCopyFileName();
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete(); // 파일을 삭제
        }
        // 데이터베이스에서 파일 정보를 삭제
        resourcesFilesRepository.delete(resourcesFiles);
    }

    //파일 다운로드 하기
    public ResponseEntity<Resource> downloadFile(ResourcesFiles resourcesFile) throws IOException {

        // 파일 경로 가져오기
        String filePath = FileLocation + resourcesFile.getCopyFileName(); // 파일이 저장된 경로

        // 파일을 바이트 배열로 읽기
        byte[] fileData = Files.readAllBytes(new File(filePath).toPath());

        // 파일 다운로드를 위한 HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();

        // 파일 이름을 UTF-8로 인코딩하여 설정
        String fileName = resourcesFile.getCopyFileName();
        String encodedFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        headers.setContentDispositionFormData("attachment", encodedFileName);

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // 파일 데이터를 ByteArrayResource로 래핑하여 응답으로 보냅니다.
        ByteArrayResource resource = new ByteArrayResource(fileData);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileData.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}




