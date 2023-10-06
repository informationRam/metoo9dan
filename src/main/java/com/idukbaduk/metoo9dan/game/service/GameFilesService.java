package com.idukbaduk.metoo9dan.game.service;

import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import com.idukbaduk.metoo9dan.education.reprository.ResourcesFilesReprository;
import com.idukbaduk.metoo9dan.game.reprository.GameContentsFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Service
@Transactional  //부모entity에 접근할때 필요함
public class GameFilesService {

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




