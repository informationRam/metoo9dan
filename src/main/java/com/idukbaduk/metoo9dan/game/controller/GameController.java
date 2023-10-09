package com.idukbaduk.metoo9dan.game.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.education.controller.EducationController;
import com.idukbaduk.metoo9dan.education.service.EducationService;
import com.idukbaduk.metoo9dan.game.service.GameFilesService;
import com.idukbaduk.metoo9dan.game.service.GameService;
import com.idukbaduk.metoo9dan.game.vaildation.GameVaildation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final GameFilesService gameFilesService;
    private final EducationService educationService;

    //게임컨텐츠 등록 폼
    @GetMapping("/addForm")
    public String gameAddForm(GameVaildation gameVaildation, Model model) {
        model.addAttribute("gameVaildation", gameVaildation);

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
    public String gameAdd(@ModelAttribute("gameVaildation") @Valid GameVaildation gameVaildation, BindingResult bindingResult,
                          @RequestParam("boardFile") MultipartFile file, @RequestParam(name = "selectedValues", required = false) String selectedValues, Model model) throws IOException {
        System.out.println("정가 : "+gameVaildation.getOriginal_price());
        System.out.println("판매가 : "+gameVaildation.getSale_price());

        // 업로드된 파일의 확장자 확인
        MultipartFile fileName = gameVaildation.getBoardFile().get(0);
        // 파일이 비어있음 null처리
        if (fileName == null || fileName.isEmpty()) {
                gameVaildation.setBoardFile(null);
        }

        if (bindingResult.hasErrors()) {
            return "game/addForm";
        }
        //정가, 할인율, 판매가가 null이면 보여주는 에러
        if(gameVaildation.getOriginal_price() == null) {
                bindingResult.rejectValue("original_price", "original_priceInCorrect", "값을 확인해주세요.");
                return "game/addForm";
        }else if(gameVaildation.getDiscount_rate() == null){
            bindingResult.rejectValue("discount_rate", "discount_rateInCorrect", "값을 확인해주세요.");
            return "game/addForm";
        } else if(gameVaildation.getSale_price() == null) {
            bindingResult.rejectValue("sale_price", "sale_priceInCorrect", "값을 확인해주세요.");
            return "game/addForm";
        }

        System.out.println("selectedValues?:"+selectedValues);
        //교육자료번호가 있으면 저장 한다.
            String[] selectedResourceNos = {};

            if (selectedValues != null && !selectedValues.isEmpty()) {
                // 선택한 값들을 ','를 기준으로 분할하여 배열로 만듭니다.
                selectedResourceNos = selectedValues.split(",");

            // 선택한 각 resourceNo에 대해 Education 객체를 조회하고 처리합니다.
            for (String resourceNoStr : selectedResourceNos) {

                int resourceNo = Integer.parseInt(resourceNoStr);
                System.out.println("resourceNo?:"+resourceNo);

                EducationalResources education = educationService.getEducation(resourceNo);
                Integer gameContentNo = gameService.toGameContents(gameVaildation).getGameContentNo();
                System.out.println("gameContentNo?:"+gameContentNo);
                GameContents gameContents = gameService.getGameContents(gameContentNo);

                gameVaildation.setContent_type("package");
                gameService.save(gameVaildation);

                educationService.pgInsert(education, gameContents);
                return "redirect:/game/list";
            }
        }

        gameVaildation.setContent_type("individual");
        gameService.save(gameVaildation);
        return "redirect:/game/list";
    }
/*

    //게임컨텐츠 목록조회
    @GetMapping("/list")
    public String gameList(Model model, @RequestParam(value = "page", defaultValue = "0") int page, GameContents gameContents) {

        Page<GameContents> gamePage = this.gameService.getList(page);

            // 게임컨텐츠에 대한 파일 정보를 가져와서 모델에 추가
            for (GameContents gamecon : gamePage.getContent()) {
                List<GameContentFiles> gameContentFilesList = gameFilesService.getGameFilesByGameContentNo(gamecon.getGameContentNo());
                gamecon.setGameContentFilesList(gameContentFilesList);
            }


        //3.Model
        model.addAttribute("gameContents", gameContents);
        model.addAttribute("gamePage", gamePage);
        return "sb-admin-7.0.4/tables";
    }
*/


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

        System.out.println("allGameContents?: " + allGameContents);

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
        GameVaildation gameVaildation = gameService.getContentVaildation(gameContents);

        model.addAttribute("gameVaildation", gameVaildation);
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
