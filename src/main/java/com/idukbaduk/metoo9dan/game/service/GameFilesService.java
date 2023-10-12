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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional  //부모entity에 접근할때 필요함
public class GameFilesService {
    private String fileUrl = "/Users/ryuahn/Desktop/baduk/";     //mac 파일 지정 C:/baduk

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

    //저장처리 (파일도 저장함)
    public void save(GameContents gameContents, GameValidation gameValidation) throws IOException {
        for (MultipartFile boardFile : gameValidation.getBoardFile()) {
            GameContentFiles gameContentFiles = new GameContentFiles(); // 이미지 저장을 위한 객체 생성

            String originalFileName = boardFile.getOriginalFilename(); //파일이름을 가져옴
            String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String copyFileName = todayDate + "_" + originalFileName;    //파일저장명 'yyyyMMddHHmmss+원본파일명'
            String savePath = fileUrl + copyFileName;   //mac 파일 지정 C:/baduk
            boardFile.transferTo(new File(savePath));   //파일저장 처리

            gameContentFiles.setGameContents(gameContents);
            gameContentFiles.setOriginFileName(originalFileName);
            gameContentFiles.setCopyFileName(copyFileName);
            gameContentsFileRepository.save(gameContentFiles);
        }
    }
    public void deleteFile(Integer fileNo) {
        GameContentFiles gameContentFiles = gameContentsFileRepository.findById(fileNo).get();
        // 파일 삭제 로직을 구현 (예: 파일 시스템에서 삭제)

        String filePath = "/Users/ryuahn/Desktop/baduk/education/" + gameContentFiles.getCopyFileName();
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete(); // 파일을 삭제
        }
        // 데이터베이스에서 파일 정보를 삭제
        gameContentsFileRepository.delete(gameContentFiles);
    }
}




