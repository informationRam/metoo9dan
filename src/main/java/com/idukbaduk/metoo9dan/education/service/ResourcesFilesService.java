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

    //교육자료 번호로 파일 정보를 가져온다.
    public ResourcesFiles getFile(int resourcesNo) {
        ResourcesFiles byEducationalResourcesResourceNo = resourcesFilesRepository.findByEducationalResources_ResourceNo(resourcesNo);
        return byEducationalResourcesResourceNo;

    }


    public ResourcesFiles getFileByFileNo(Integer fileNo) {
        ResourcesFiles resourcesFiles = resourcesFilesRepository.findById(fileNo).get();
        return resourcesFiles;
    }


    // 파일 저장처리
    public void updateFile(EducationalResources educationalResources, EducationValidation educationValidation) throws IOException {
        ResourcesFiles resourcesFiles = getFile(educationalResources.getResourceNo());

        MultipartFile thumFile = educationValidation.getThumFile();
        MultipartFile boardFile = educationValidation.getBoardFile();

        System.out.println("thumFile?"+thumFile);
        System.out.println("boardFile?"+boardFile);
        if (!thumFile.isEmpty()) {
            String thumOriginFile = thumFile.getOriginalFilename();
            System.out.println("thumOriginFile?"+thumOriginFile);
            String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String thumCopyFileName = todayDate + "_" + thumOriginFile;
            String thumSavePath = fileUrl + thumCopyFileName;

            thumFile.transferTo(new File(thumSavePath));
            resourcesFiles.setThumOriginFileName(thumOriginFile);
            resourcesFiles.setThumOriginCopyName(thumCopyFileName);
            resourcesFiles.setFileUrl(fileUrl);
            resourcesFiles.setEducationalResources(educationalResources);

            if (!boardFile.isEmpty()) {

                String originalFileName = boardFile.getOriginalFilename();
                System.out.println("originalFileName?"+originalFileName);
                todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String copyFileName = todayDate + "_" + originalFileName;
                String savePath = fileUrl + copyFileName;

                boardFile.transferTo(new File(savePath));

                resourcesFiles.setEducationalResources(educationalResources);
                resourcesFiles.setOriginFileName(originalFileName);
                resourcesFiles.setCopyFileName(copyFileName);
            }
            resourcesFilesRepository.save(resourcesFiles);
        }
    }




    // 파일 저장처리
    public void save(EducationalResources educationalResources, EducationValidation educationValidation) throws IOException {

        MultipartFile thumFile = educationValidation.getThumFile();
        MultipartFile boardFile = educationValidation.getBoardFile();

        System.out.println("thumFile?"+thumFile);
        System.out.println("boardFile?"+boardFile);
        if (!thumFile.isEmpty()) {
            String thumOriginFile = thumFile.getOriginalFilename();
            System.out.println("thumOriginFile?"+thumOriginFile);
            String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String thumCopyFileName = todayDate + "_" + thumOriginFile;
            String thumSavePath = fileUrl + thumCopyFileName;

            thumFile.transferTo(new File(thumSavePath));

            ResourcesFiles resourcesFiles = new ResourcesFiles();

            resourcesFiles.setThumOriginFileName(thumOriginFile);
            resourcesFiles.setThumOriginCopyName(thumCopyFileName);
            resourcesFiles.setFileUrl(fileUrl);
            resourcesFiles.setEducationalResources(educationalResources);

            if (!boardFile.isEmpty()) {

                String originalFileName = boardFile.getOriginalFilename();
                System.out.println("originalFileName?"+originalFileName);
                todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String copyFileName = todayDate + "_" + originalFileName;
                String savePath = fileUrl + copyFileName;

                boardFile.transferTo(new File(savePath));

                resourcesFiles.setEducationalResources(educationalResources);
                resourcesFiles.setOriginFileName(originalFileName);
                resourcesFiles.setCopyFileName(copyFileName);
            }
            resourcesFilesRepository.save(resourcesFiles);
        }
    }

    // 삭제된 파일 처리
    public void deleteBordFile(ResourcesFiles resourcesFiles) {
        if (resourcesFiles != null && !resourcesFiles.equals("")) {
            // 파일을 서버에서 삭제하는 로직을 구현
            // 파일 삭제 로직을 구현 (예: 파일 시스템에서 삭제)
            String filePath = fileUrl + resourcesFiles.getCopyFileName();
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                file.delete(); // 파일을 삭제
            }
            System.out.println("resourcesFiles.getThumOriginFileName()?"+resourcesFiles.getThumOriginFileName());
            // 썸네일 값이 없도 데이터베이스에서 정보를 삭제 , 아니면 수정
            System.out.println("resourcesFiles.getOriginFileName()?" + resourcesFiles.getOriginFileName());
// ...
            if (resourcesFiles.getOriginFileName() == null || resourcesFiles.getOriginFileName().isEmpty()) {
                resourcesFilesRepository.delete(resourcesFiles);
            }

            resourcesFiles.setOriginFileName(null);
            resourcesFiles.setCopyFileName(null);
            resourcesFilesRepository.save(resourcesFiles);
        }
    }

    public void deleteThumFile(ResourcesFiles resourcesFiles) {
        if (resourcesFiles != null && !resourcesFiles.equals("")) {
            // 파일을 서버에서 삭제하는 로직을 구현
            // 파일 삭제 로직을 구현 (예: 파일 시스템에서 삭제)
            String filePath = fileUrl + resourcesFiles.getThumOriginCopyName();
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                file.delete(); // 파일을 삭제
            }
            System.out.println("resourcesFiles.getOriginFileName()?"+resourcesFiles.getOriginFileName());
            // 교육자료 값이 없으면 , 데이터베이스에서 정보를 삭제 , 아니면 수정
// ...
            if (resourcesFiles.getOriginFileName().isEmpty() && resourcesFiles.getOriginFileName() == null) {
                resourcesFilesRepository.delete(resourcesFiles);
            }

            resourcesFiles.setThumOriginFileName(null);
            resourcesFiles.setThumOriginCopyName(null);
            resourcesFilesRepository.save(resourcesFiles);
        }
    }

    //파일 다운로드 하기
    public ResponseEntity<Resource> downloadFile(ResourcesFiles resourcesFile, String cofyfileName) throws IOException {

        String filePath = null;

        if (resourcesFile != null) {
            if (resourcesFile.getThumOriginCopyName() != null && resourcesFile.getThumOriginCopyName().equals(cofyfileName)) {
                filePath = fileUrl + cofyfileName; // 파일이 저장된 경로
            } else if (resourcesFile.getCopyFileName() != null && resourcesFile.getCopyFileName().equals(cofyfileName)) {
                filePath = fileUrl + cofyfileName; // 파일이 저장된 경로
            }
        }

        if (filePath != null) {

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
        return null; //오류 처리 필요
    }

    // 전체 파일 삭제 처리
    public void alldeleteFile(ResourcesFiles resourcesFiles) {
        if (resourcesFiles != null && !resourcesFiles.equals("")) {
            // 파일을 서버에서 삭제하는 로직을 구현
            // 파일 삭제 로직을 구현 (예: 파일 시스템에서 삭제)

            if (resourcesFiles.getThumOriginCopyName() != null) {
                String filePath = fileUrl + resourcesFiles.getThumOriginCopyName();
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    file.delete(); // 파일을 삭제
                }
            }

            if (resourcesFiles.getOriginFileName() != null) {
                String filePath = fileUrl + resourcesFiles.getCopyFileName();
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    file.delete(); // 파일을 삭제
                }
            }
            resourcesFilesRepository.delete(resourcesFiles);
        }
    }

}



