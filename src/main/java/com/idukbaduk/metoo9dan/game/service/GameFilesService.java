package com.idukbaduk.metoo9dan.game.service;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import com.idukbaduk.metoo9dan.education.reprository.ResourcesFilesReprository;
import com.idukbaduk.metoo9dan.education.vaildation.EducationValidation;
import com.idukbaduk.metoo9dan.game.reprository.GameContentsFileRepository;
import com.idukbaduk.metoo9dan.game.vaildation.GameValidation;
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
public class GameFilesService {
    private String fileUrl = "C:/upload/game/";     //mac 파일 지정 C:/baduk

    @Autowired
    private final GameContentsFileRepository gameContentsFileRepository;

    @Autowired
    public GameFilesService(GameContentsFileRepository gameContentsFileRepository) {
        this.gameContentsFileRepository = gameContentsFileRepository;
    }

    public List<GameContentFiles> getGameFilesByGameContentNo(Integer gameContentNo) {
        return gameContentsFileRepository.findByGameContents_GameContentNo(gameContentNo);
    }

    public GameContentFiles getFileByFileNo(Integer fileNo) {
        GameContentFiles gameContentFiles = gameContentsFileRepository.findById(fileNo).get();
        return gameContentFiles;
    }

    public void save(GameContents gameContents, GameValidation gameValidation) throws IOException {
        for (MultipartFile boardFile : gameValidation.getBoardFile()) {
            GameContentFiles gameContentFiles = new GameContentFiles(); // 이미지 저장을 위한 객체 생성

            String originalFileName = boardFile.getOriginalFilename();
            String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String copyFileName = todayDate + "_" + originalFileName;
            String savePath = fileUrl + copyFileName;

            try {
                // Check if the directory exists, if not, create it
                File saveDirectory = new File(fileUrl);
                if (!saveDirectory.exists()) {
                    saveDirectory.mkdirs();
                }

                boardFile.transferTo(new File(savePath));
                gameContentFiles.setGameContents(gameContents);
                gameContentFiles.setOriginFileName(originalFileName);
                gameContentFiles.setCopyFileName(copyFileName);
                gameContentsFileRepository.save(gameContentFiles);
            } catch (IOException e) {
                // Handle the exception appropriately, e.g., log it or throw a custom exception
                e.printStackTrace();
                throw e; // You can also throw a custom exception here
            }
        }
    }



    // 파일 삭제로직
    public void deleteFile(Integer gameconNo) {

        List<GameContentFiles> byGameContentsGameContentNo = gameContentsFileRepository.findByGameContents_GameContentNo(gameconNo);
       for(GameContentFiles gameContentFile : byGameContentsGameContentNo){

           GameContentFiles gameContentFiles = gameContentsFileRepository.findById(gameContentFile.getFileNo()).get();
           // 파일 삭제 로직을 구현 (예: 파일 시스템에서 삭제)

           String filePath = fileUrl + gameContentFiles.getCopyFileName();
           File file = new File(filePath);
           if (file.exists() && file.isFile()) {
               file.delete(); // 파일을 삭제
           }
           // 데이터베이스에서 파일 정보를 삭제
           gameContentsFileRepository.delete(gameContentFiles);

       }
    }

    //파일 다운로드 하기
    public ResponseEntity<Resource> downloadFile(GameContentFiles gameContentFiles, String cofyfileName) throws IOException {

        System.out.println("gameContentFiles?"+gameContentFiles);
        System.out.println("cofyfileName?"+cofyfileName);
        if (gameContentFiles != null) {
            String filePath = fileUrl + cofyfileName; // 파일이 저장된 경로

            if (filePath != null) {

                // 파일을 바이트 배열로 읽기
                byte[] fileData = Files.readAllBytes(new File(filePath).toPath());

                // 파일 다운로드를 위한 HTTP 헤더 설정
                HttpHeaders headers = new HttpHeaders();

                // 파일 이름을 UTF-8로 인코딩하여 설정
                String fileName = gameContentFiles.getCopyFileName();
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
        return null; //오류 처리 필요
    }




}




