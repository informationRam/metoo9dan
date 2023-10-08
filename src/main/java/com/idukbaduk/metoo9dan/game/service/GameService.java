package com.idukbaduk.metoo9dan.game.service;

import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import com.idukbaduk.metoo9dan.game.reprository.GameContentsFileRepository;
import com.idukbaduk.metoo9dan.game.reprository.GameRepository;
import com.idukbaduk.metoo9dan.game.vaildation.GameContentFilesVaildation;
import com.idukbaduk.metoo9dan.game.vaildation.GameVaildation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
public class GameService {

    private String uploadDir;

    private final GameRepository gameRepository;
    private final GameContentsFileRepository gameContentsFileRepository;

    //게임콘텐츠등록 (파일같이저장)
    public void save(GameVaildation gameVaildation) throws IOException {
       GameContents gameContents = new GameContents();

        //파일이 있다면
        if(gameVaildation.getBoardFile() != null) {

            gameContents.setGameName(gameVaildation.getGame_name());
            gameContents.setDifficulty(gameVaildation.getDifficulty());
            gameContents.setSubscriptionDuration(gameVaildation.getSubscription_duration());
            gameContents.setMaxSubscribers(gameVaildation.getMax_subscribers());
            gameContents.setOriginalPrice(gameVaildation.getOriginal_price());
            gameContents.setDiscountRate(gameVaildation.getDiscount_rate());
            gameContents.setSalePrice(gameVaildation.getSale_price());
            gameContents.setPackageDetails(gameVaildation.getPackage_details());
            gameContents.setCreationDate(LocalDateTime.now());
            gameContents.setStatus(gameVaildation.getStatus());    //게시글상태
            gameRepository.save(gameContents);

            //파일 저장

            for (MultipartFile boardFile : gameVaildation.getBoardFile()) {
                GameContentFiles gameContentFiles = new GameContentFiles();
                gameContentFiles.setGameContents(gameContents);
                String originalFileName = boardFile.getOriginalFilename(); //파일이름을 가져옴
                String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String copyFileName = todayDate + "_" + originalFileName;    //파일저장명 'yyyyMMddHHmmss+원본파일명'
                String uploadDirectory = "/Users/ryuahn/Desktop/baduk/";
                String savePath = uploadDirectory + copyFileName; //mac 파일 지정 C:/baduk
                System.out.println("uploadDirectory?:" + uploadDirectory);
                boardFile.transferTo(new File(savePath));   //파일저장 처리

                gameContentFiles.setOriginFileName(originalFileName);
                gameContentFiles.setCopyFileName(copyFileName);
                gameContentsFileRepository.save(gameContentFiles);
            }
        }else {

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
        }
    }
    //게임목록조회 (페이징처리)
    public Page<GameContents> getList(int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("creationDate"));     //등록일순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return gameRepository.findAll(pageable);
    }

    // 페이지네이션을 사용하지 않고 모든 게임콘텐츠 조회
    public List<GameContents> getAllGameContents() {
        return gameRepository.findAll(); // 페이지네이션 없이 모든 게임콘텐츠를 가져옵니다.
    }

    //gameNo값을 주면 GameContents값 전체 조회
    public GameContents getGameContents(int gameContentNo){
        GameContents gameContents = gameRepository.findById(gameContentNo).get();
        return gameContents;
    }

    public GameVaildation getContentVaildation(GameContents gameContents) {
        GameVaildation gameVaildation = new GameVaildation();
        gameVaildation.setGame_name(gameContents.getGameName());
        gameVaildation.setDifficulty(gameContents.getDifficulty());
        gameVaildation.setSubscription_duration(gameContents.getSubscriptionDuration());
        gameVaildation.setMax_subscribers(gameContents.getMaxSubscribers());
        gameVaildation.setOriginal_price(gameContents.getOriginalPrice());
        gameVaildation.setDiscount_rate(gameContents.getDiscountRate());
        gameVaildation.setSale_price(gameContents.getSalePrice());
        return gameVaildation;
    }

    public GameContents toGameContents(GameVaildation gameVaildation) {
        GameContents gameContents = new GameContents();
        gameContents.setGameContentNo(gameContents.getGameContentNo());
        return gameContents;
    }





  /*  //gameNo값을 주면 ContentsFile 값 전체 조회
    public GameContents getGameContentsFile(int gameContentNo){
        GameContentFiles gameContentFiles = gameContentsFileRepository.findAllById(gameContentNo).get();
        return getGameContentsFile;
    }*/

    //게임 컨텐츠 삭제
    public void delete(GameContents gameContents) {
        gameContents.setStatus("N");
        gameRepository.save(gameContents);  //게시글상태를 'N'으로 변경한다.
    }
}
