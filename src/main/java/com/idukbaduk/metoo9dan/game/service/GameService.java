package com.idukbaduk.metoo9dan.game.service;

import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.game.reprository.GameContentsFileRepository;
import com.idukbaduk.metoo9dan.game.reprository.GameRepository;
import com.idukbaduk.metoo9dan.game.vaildation.GameContentFilesVaildation;
import com.idukbaduk.metoo9dan.game.vaildation.GameVaildation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GameContentsFileRepository gameContentsFileRepository;

    //게임콘텐츠등록 (파일같이저장)
    public void gameAdd(GameVaildation gameVaildation){
       GameContents gameContents = new GameContents();

        gameContents.setGameName(gameVaildation.getGame_name());
        gameContents.setDifficulty(gameVaildation.getDifficulty());
        gameContents.setSubscriptionDuration(gameVaildation.getSubscription_duration());
        gameContents.setMaxSubscribers(gameVaildation.getMax_subscribers());
        gameContents.setOriginalPrice(gameVaildation.getOriginal_price());
        gameContents.setDiscountRate(gameVaildation.getDiscount_rate());
        gameContents.setSalePrice(gameVaildation.getSale_price());
        gameContents.setPackageDetails(gameVaildation.getPackage_details());
        gameContents.setCreationDate(LocalDateTime.now());
        gameContents.setStatus("Y");    //게시글상태
        gameContents.setContentType("package");
        gameRepository.save(gameContents);

        GameContentFiles gameContentFiles = new GameContentFiles();
        gameContentFiles.setGameContents(gameContents);

        //파일을 첨부하지않으면 null로 저장 된다.
        if(gameContentFiles != null){
            String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String originalFileName = gameVaildation.getOrigin_file_name();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String copyFileName = todayDate + fileExtension;
            gameContentFiles.setOriginFileName(originalFileName);
            gameContentFiles.setCopyFileName(copyFileName);
            gameContentsFileRepository.save(gameContentFiles);
        }else {
            gameContentFiles.setOriginFileName(null);
            gameContentFiles.setCopyFileName(null);
            gameContentsFileRepository.save(gameContentFiles);
        }

    }


}
