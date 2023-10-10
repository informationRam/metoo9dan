package com.idukbaduk.metoo9dan.game.controller;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.education.service.EducationService;
import com.idukbaduk.metoo9dan.education.vaildation.EducationValidation;
import com.idukbaduk.metoo9dan.game.service.GameFilesService;
import com.idukbaduk.metoo9dan.game.service.GameService;
import com.idukbaduk.metoo9dan.game.vaildation.GameValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final GameFilesService gameFilesService;
    private final EducationService educationService;
    private GameContents getgameContents;

    //게임컨텐츠 등록 폼 (교육자료 함께 저장시 교육자료가 update 및 생성된다.)
    @GetMapping("/addForm")
    public String gameAddForm(GameValidation gameValidation, Model model) {
        model.addAttribute("gameValidation", gameValidation);

        List<EducationalResources> allEducation = educationService.getAllEducation();
        model.addAttribute("allEducation", allEducation);
        return "game/addForm";
    }

    //게임등록 폼에서 교육자료 검색 Ajax처리
    @Transactional(readOnly = true)
    @PostMapping("/search")
    @ResponseBody
    public List<EducationalResources> getSearchResults(@RequestParam(value = "searchKeyword",required = false) String searchKeyword) {
            System.out.println("searchKeyword:"+ searchKeyword);
            List<EducationalResources> allEducation;

        if(searchKeyword.equals("all")){
            System.out.println("여결와야지?");
            allEducation = educationService.getAllEducation();
            System.out.println("allEducation?:" +allEducation);
            return allEducation;
        }else {
            System.out.println("여길?");
            allEducation = educationService.search(searchKeyword);

            if (allEducation == null) {
                allEducation = new ArrayList<>(); // 빈 목록을 반환하도록 설정
            }
            return allEducation;
        }
    }

    //게임 컨텐츠 등록 처리
    @PostMapping("/add")
    @Transactional
    public String gameAdd(@ModelAttribute("gameValidation") @Valid GameValidation gameValidation, BindingResult bindingResult,
                          @RequestParam("boardFile") MultipartFile file, @RequestParam(name = "selectedValues", required = false) String selectedValues, Model model) throws IOException {


        if (bindingResult.hasErrors()) {
            return "game/addForm";
        }
        //정가, 할인율, 판매가가 null이면 보여주는 에러
        if(gameValidation.getOriginal_price() == null) {
                bindingResult.rejectValue("original_price", "original_priceInCorrect", "값을 확인해주세요.");
                return "game/addForm";
        }else if(gameValidation.getDiscount_rate() == null){
            bindingResult.rejectValue("discount_rate", "discount_rateInCorrect", "값을 확인해주세요.");
            return "game/addForm";
        } else if(gameValidation.getSale_price() == null) {
            bindingResult.rejectValue("sale_price", "sale_priceInCorrect", "값을 확인해주세요.");
            return "game/addForm";
        }

        //교육자료번호가 있으면 저장 한다.
        String[] selectedResourceNos = {};

        if (selectedValues != null && !selectedValues.isEmpty()) {
            selectedResourceNos = selectedValues.split(",");

            //게임을 Pakage로 저장한다.
            GameValidation savedGameValidation = gameService.savePakage(gameValidation);

            GameContents gameContents = gameService.getGameContents(savedGameValidation.getGame_no());

                // 선택한 각 resourceNo에 대해 Education 객체를 조회하고 처리합니다.
                for (String resourceNoStr : selectedResourceNos) {

                    int resourceNo = Integer.parseInt(resourceNoStr);

                    // resourceNo값을 넣어 EducationalResources 값을 가져온다.
                    EducationalResources education = educationService.getEducation(resourceNo);

                    //저장한 gameContentNo(pk)를 가져온다. 가져온 gameContentNo값을 가지고 gameContents를 가져온다.
                    getgameContents = gameService.getGameContents(savedGameValidation.getGame_no());

                    System.out.println("education.getGameContents()? : "+education.getGameContents());

                    //선택한 교육자료의 GameContents값이 없으면 update.
                    if (education.getGameContents() == null || education.getGameContents().equals("")) {
                        educationService.pgInsert(education, gameContents);
                    } else {
                        EducationValidation educationValidation = educationService.toEducationValidation(education);
                        educationService.copysave(educationValidation, getgameContents);
                    }
                }
            }else {
                // save Individual로 저장.
                gameService.saveIndividual(gameValidation);
            }
        // 업로드된 파일의 확장자 확인
        MultipartFile fileName = gameValidation.getBoardFile().get(0);
        //파일여부확인
        if (fileName != null && !file.isEmpty()) {
            gameFilesService.save(getgameContents, gameValidation);
        }
        return "redirect:/game/list";
    }

    //게임콘텐츠 목록조회
    @GetMapping("/list")
    public String gameList(Model model) {
        List<GameContents> allGameContents = gameService.getAllGameContents();

        System.out.println("allGameContents1?: " + allGameContents);
        // 각 게임 컨텐츠에 대한 파일 정보를 가져와서 모델에 추가
        for (GameContents gameContents : allGameContents) {
            System.out.println("gameContents?: " + gameContents);
            List<GameContentFiles> gameContentFilesList = gameFilesService.getGameFilesByGameContentNo(gameContents.getGameContentNo());
            gameContents.setGameContentFilesList(gameContentFilesList);
        }
        // Model
        model.addAttribute("gameList", allGameContents);
        return "sb-admin-7.0.4/tables";
    }

    // 게임 상세조회
    @GetMapping("/detail/{gameContentNo}")
    public String gameDetail(@PathVariable Integer gameContentNo, Model model) {
        GameContents gameContents = gameService.getGameContents(gameContentNo);

        //3.Model
        model.addAttribute("gameContents", gameContents);
        return "game/detailForm";
    }

    // 게임콘텐츠 수정폼
    @GetMapping("/modify/{gameContentNo}")
    public String gameModify(@PathVariable Integer gameContentNo, Model model) {
        GameContents gameContents = gameService.getGameContents(gameContentNo);
        GameValidation gameValidation = gameService.getContentValidation(gameContents);

        model.addAttribute("gameVaildation", gameValidation);
        return "game/detailForm";
    }

    //  삭제하기
    @GetMapping("/delete/{gameContentNo}")
    public String gameDelete(@PathVariable("gameContentNo") Integer gameContentNo) {
        // 현재 사용자의 인증 정보를 가져옴
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 현재 사용자의 권한 중 하나라도 "ROLE_ADMIN"이 아니라면
//        if (!authentication.getAuthorities().stream()
//                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"권한이 없습니다.");
//        }
        GameContents gameContents = gameService.getGameContents(gameContentNo);
        gameService.delete(gameContents);
        return "redirect:/game/list";    //목록으로이동
    }

    //  삭제하기
    @GetMapping("/test")
    public String test() {
        return "sb-admin-7.0.4/charts";    //목록으로이동
    }


}//class
