package com.idukbaduk.metoo9dan.game.service;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.game.reprository.GameContentsFileRepository;
import com.idukbaduk.metoo9dan.game.reprository.GameRepository;
import com.idukbaduk.metoo9dan.game.vaildation.GameValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final GameRepository gameRepository;
    private final GameContentsFileRepository gameContentsFileRepository;
    private final GameFilesService gameFilesService;

    //게임콘텐츠등록 (파일같이저장)
    @Transactional
    public GameValidation savePakage(GameValidation gameValidation) throws IOException {
        //파일이 있다면 처리
        GameContents gameContents = toGameContents(gameValidation);
        GameContents getgameContents = null;

        gameContents.setContentType("package");
        getgameContents = gameRepository.save(gameContents);

        // 저장한 gameContents의 GameContentNo(pk)를 return한다.
        gameValidation.setGame_no(getgameContents.getGameContentNo());
        return gameValidation;
    }



@Transactional
public void saveIndividual(GameValidation gameValidation) {
        // gameValidation.setContent_type("individual");
    try {
        GameContents gameContents = toGameContents(gameValidation);
        gameContents.setContentType("individual");
        gameRepository.save(gameContents);   //교육자료를 저장
    }catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Failed to save educational resources: " + e.getMessage());
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

    public GameValidation getContentValidation(GameContents gameContents) {
        GameValidation gameValidation = new GameValidation();
        gameValidation.setGame_name(gameContents.getGameName());
        gameValidation.setDifficulty(gameContents.getDifficulty());
        gameValidation.setSubscription_duration(gameContents.getSubscriptionDuration());
        gameValidation.setMax_subscribers(gameContents.getMaxSubscribers());
        gameValidation.setOriginal_price(gameContents.getOriginalPrice());
        gameValidation.setDiscount_rate(gameContents.getDiscountRate());
        gameValidation.setSale_price(gameContents.getSalePrice());
        return gameValidation;
    }

    public GameContents toGameContents(GameValidation gameValidation) {
        GameContents gameContents = new GameContents();

        gameContents.setGameName(gameValidation.getGame_name());
        gameContents.setDifficulty(gameValidation.getDifficulty());
        gameContents.setSubscriptionDuration(gameValidation.getSubscription_duration());
        gameContents.setMaxSubscribers(gameValidation.getMax_subscribers());
        gameContents.setOriginalPrice(gameValidation.getOriginal_price());
        gameContents.setDiscountRate(gameValidation.getDiscount_rate());
        gameContents.setSalePrice(gameValidation.getSale_price());
        gameContents.setPackageDetails(gameValidation.getPackage_details());
        gameContents.setCreationDate(LocalDateTime.now());
        gameContents.setStatus("Y");    //게시글상태
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
