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

    private String fileUrl = "/Users/ryuahn/Desktop/baduk/education/";

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

    // 저장처리 (썸네일 저장함)
    public void save(EducationalResources educationalResources, EducationValidation educationValidation) throws IOException {

        MultipartFile thumFile = educationValidation.getThumFile();
        if (!thumFile.isEmpty()) {
            String thumOriginFile = thumFile.getOriginalFilename();
            String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String thumCopyFileName = todayDate + "_" + thumOriginFile;
            String thumSavePath = fileUrl + thumCopyFileName;

            thumFile.transferTo(new File(thumSavePath));

            ResourcesFiles thumResourcesFile = new ResourcesFiles();
            thumResourcesFile.setThumOriginFileName(thumOriginFile);
            thumResourcesFile.setThumOriginCopyName(thumCopyFileName);
            thumResourcesFile.setFileUrl(fileUrl);
            thumResourcesFile.setEducationalResources(educationalResources);
            resourcesFilesRepository.save(thumResourcesFile);
        }

        for (MultipartFile boardFile : educationValidation.getBoardFile()) {
            if (!boardFile.isEmpty()) {
                String originalFileName = boardFile.getOriginalFilename();
                String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String copyFileName = todayDate + "_" + originalFileName;
                String savePath = fileUrl + copyFileName;

                boardFile.transferTo(new File(savePath));

                ResourcesFiles resourcesFiles = new ResourcesFiles();
                resourcesFiles.setFileUrl(fileUrl);
                resourcesFiles.setEducationalResources(educationalResources);
                resourcesFiles.setOriginFileName(originalFileName);
                resourcesFiles.setCopyFileName(copyFileName);
                resourcesFilesRepository.save(resourcesFiles);
            }
        }
    }

    // 삭제된 파일 처리
    public void deleteFile(List<Integer> deletedFiles) {
        for (Integer fileNo : deletedFiles) {
            if (fileNo != null && !deletedFiles.isEmpty()) {
                System.out.println("fileNo?: " + fileNo);
                // 파일을 서버에서 삭제하는 로직을 구현
                ResourcesFiles resourcesFile = getFileByFileNo(fileNo);
                // 파일 삭제 로직을 구현 (예: 파일 시스템에서 삭제)
                String filePath = fileUrl + resourcesFile.getCopyFileName();
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    file.delete(); // 파일을 삭제
                }
                // 데이터베이스에서 파일 정보를 삭제
                ResourcesFiles resourcesFiles = resourcesFilesRepository.findById(fileNo).get();
                resourcesFilesRepository.delete(resourcesFiles);
            }
        }
    }

    //파일 다운로드 하기
    public ResponseEntity<Resource> downloadFile(ResourcesFiles resourcesFile) throws IOException {

        // 파일 경로 가져오기
        String filePath = fileUrl + resourcesFile.getCopyFileName(); // 파일이 저장된 경로

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




