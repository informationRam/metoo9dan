package com.idukbaduk.metoo9dan.game.controller;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.education.service.EducationService;
import com.idukbaduk.metoo9dan.education.vaildation.EducationValidation;
import com.idukbaduk.metoo9dan.game.service.GameFilesService;
import com.idukbaduk.metoo9dan.game.service.GameService;
import com.idukbaduk.metoo9dan.game.vaildation.GameValidation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                          @RequestParam("boardFile") MultipartFile file, @RequestParam(name = "selectedValues", required = false) String selectedValues) throws IOException {


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

    // ----------------- 테스트 --------------------!


    // page 1
    @GetMapping("/page1")
    public String getPage1(Model model) {
        model.addAttribute("gameValidation", new GameValidation());
        return "game/page1";
    }

    @PostMapping("/page1")
    public String handlePage1Post(@ModelAttribute("gameValidation") @Valid GameValidation gameValidation, BindingResult bindingResult
                                      ,@RequestParam("boardFile") MultipartFile[] files, HttpSession session) {

        // 파일 처리 로직
        List<String> originFileNames = new ArrayList<>();

        for (MultipartFile boardFile : files) {
            String originalFileName = boardFile.getOriginalFilename();
            String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            originFileNames.add(originalFileName); // originFileName을 리스트에 추가
        }

        // 세션에 originFileNames 리스트를 저장
        session.setAttribute("gameContentFiles", originFileNames);
        session.setAttribute("gameValidationPage1", gameValidation);

        return "redirect:/game/page2"; // Page2로 리다이렉트
    }

    // page 2
    @GetMapping("/page2")
    public String getPage2(@ModelAttribute("gameValidation") GameValidation gameValidation, Model model) {
        List<EducationalResources> allEducation = educationService.getAllEducation();

       /* model.addAttribute("gameValidation",gameValidation);*/
        model.addAttribute("allEducation", allEducation);
        return "game/page2";
    }

    // 게임등록 폼에서 교육자료 선택
    @Transactional(readOnly = true)
    @PostMapping("/selectEdu")
    @ResponseBody
    public Map<String,Object> getEduResults(@RequestParam(name = "selectedValues", required = false) String selectedValues, HttpSession session) {
        String[] selectedResourceNos = selectedValues.split(",");
        List<EducationalResources> educationalResources = new ArrayList<>();

        for (String resourceNoStr : selectedResourceNos) {
            int resourceNo = Integer.parseInt(resourceNoStr);
            EducationalResources education = educationService.getEducation(resourceNo);
            educationalResources.add(education);
        }

        Map<String, Object> response = new HashMap<>();

        if (educationalResources.isEmpty()) {
            // 저장 실패 메시지를 맵에 추가
            response.put("message", "실패하였습니다.");
        } else {
            // 세션에 educationalResources를 저장
            session.setAttribute("educationalResources", educationalResources);
            // 성공 메시지를 맵에 추가
            response.put("message", "저장되었습니다.");
        }
        return response;
    }

    //page2
    @PostMapping("/page2")
    public String handlePage2Post(@ModelAttribute("gameValidation") @Valid GameValidation gameValidation, BindingResult bindingResult,
                                  HttpSession session, Model model) {

     /*   model.addAttribute("gameValidation", gameValidation);
        // page2에서 입력 받은 데이터를 세션에 추가 또는 업데이트
        GameValidation gameValidationPage1 = (GameValidation) session.getAttribute("gameValidationPage1");
        gameValidationPage1.setOriginal_price(gameValidation.getOriginal_price());
        gameValidationPage1.setDiscount_rate(gameValidation.getDiscount_rate());
        gameValidationPage1.setSale_price(gameValidation.getSale_price());

        session.setAttribute("gameValidation", gameValidationPage1);*/
        return "redirect:/game/page3"; // Page3로 리다이렉트
    }

// page3
    @GetMapping("/page3")
    public String getPage3(Model model,HttpSession session) {

        // 세션에서 정보를 가져옵니다.
        List<EducationalResources> educationalResources = (List<EducationalResources>) session.getAttribute("educationalResources");
        List<String> originFileNames = (List<String>) session.getAttribute("gameContentFiles");
        GameValidation gameValidation = (GameValidation) session.getAttribute("gameValidation");

        // 모델에 추가하여 뷰로 전달합니다.
        model.addAttribute("educationalResources", educationalResources);
        model.addAttribute("originFileNames", originFileNames);
        model.addAttribute("gameValidation", gameValidation);

        return "game/page3";
    }


    // 저장 처리 로직
    @PostMapping("/save")
    public String saveGame(@ModelAttribute("gameValidation") @Valid GameValidation gameValidation,BindingResult bindingResult,
                    HttpSession session, @RequestParam("boardFile") MultipartFile file, Model model) throws IOException {
        // page3에서 확인 버튼을 눌렀을 때 서버에 저장
        // gameValidation 객체에 필요한 데이터가 모두 채워져 있을 것입니다.

        //정가, 할인율, 판매가가 null이면 보여주는 에러
        if(gameValidation.getOriginal_price() == null) {
            bindingResult.rejectValue("original_price", "original_priceInCorrect", "값을 확인해주세요.");
            return "game/page3";
        }

        //세션에서 값을 가져옴
        List<String> originFileNames = (List<String>) session.getAttribute("gameContentFiles");
        //세션에 저장된 educationalResources 값을 가져온다
        List<EducationalResources> educationalResources = (List<EducationalResources>) session.getAttribute("educationalResources");


        // page3에서 입력 받은 데이터를 세션에 추가 하여 session으로 저장
        model.addAttribute("gameValidation", gameValidation);
        GameValidation gameValidationPage1 = (GameValidation) session.getAttribute("gameValidationPage1");
        gameValidationPage1.setOriginal_price(gameValidation.getOriginal_price());
        gameValidationPage1.setDiscount_rate(gameValidation.getDiscount_rate());
        gameValidationPage1.setSale_price(gameValidation.getSale_price());
        session.setAttribute("gameValidation", gameValidationPage1);

        gameValidation = (GameValidation) session.getAttribute("gameValidation");

        // gameValidation 값을 gameContents Entity로 변경한다.
        GameContents gameContents = gameService.toGameContents(gameValidation);

        // educationalResources가 존재하면
        if (!educationalResources.isEmpty()) {
            //게임을 Pakage로 저장한다.
            GameValidation savedGameValidation = gameService.savePakage(gameValidation);
            gameContents = gameService.getGameContents(savedGameValidation.getGame_no());

            // 선택한 각 resourceNo에 대해 Education 객체를 조회하고 처리합니다.
            for (EducationalResources resource : educationalResources) {
                    int resourceNo = resource.getResourceNo();

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

        System.out.println("gameValidation: "+gameValidation);
        // 저장 후 세션에서 데이터 삭제
        session.removeAttribute("gameValidationPage1");
        return "redirect:/game/list";
    }



}//class
