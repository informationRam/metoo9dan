package com.idukbaduk.metoo9dan.game.controller;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import com.idukbaduk.metoo9dan.education.service.EducationService;
import com.idukbaduk.metoo9dan.education.service.ResourcesFilesService;
import com.idukbaduk.metoo9dan.education.vaildation.EducationValidation;
import com.idukbaduk.metoo9dan.game.service.GameFilesService;
import com.idukbaduk.metoo9dan.game.service.GameService;
import com.idukbaduk.metoo9dan.game.vaildation.GameValidation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
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
    private final ResourcesFilesService resourcesFilesService;

    //게임컨텐츠 등록 폼 (교육자료 함께 저장시 교육자료가 update 및 생성된다.)
    @GetMapping("/addForm")
    public String gameAddForm(GameValidation gameValidation, Model model, HttpSession session) {

        List<EducationalResources> educationalResources = new ArrayList<>();

     /*   List<EducationalResources> allEducation = educationService.getAllEducation();*/

        List<EducationalResources> allEducation = educationService.getDistinct();
        // 교육자료에 대한 파일 정보를 가져와서 모델에 추가
        for (EducationalResources education : allEducation) {
            ResourcesFiles resourcesFilesList = resourcesFilesService.getFile(education.getResourceNo());
            education.setResourcesFilesList(resourcesFilesList);
        }

        session.setAttribute("educationalResources", educationalResources);
        model.addAttribute("allEducation", allEducation);
        model.addAttribute("gameValidation", gameValidation);
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
            allEducation = educationService.getDistinct();
            System.out.println("allEducation?:" +allEducation);
            return allEducation;
        }else {
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
                          @RequestParam("boardFile") MultipartFile file,
                          @RequestParam(name = "selectedValues", required = false) String selectedValues,Model model) throws IOException {

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

    //게임콘텐츠 목록조회
    @GetMapping("/list2")
    public String gameList(Model model, @RequestParam(value = "page", defaultValue = "0") int page, GameContents gameContents,@RequestParam(required = false, defaultValue = "") String searchText) {

        // 게임컨텐츠 목록 조회
        Page<GameContents> gamePage = this.gameService.gameSearch(searchText, searchText, page);

        for (GameContents gamecon : gamePage.getContent()) {
            // 게임컨텐츠에 대한 파일 정보 가져오기
            List<GameContentFiles> gameContentFilesList = gameFilesService.getGameFilesByGameContentNo(gamecon.getGameContentNo());
            gamecon.setGameContentFilesList(gameContentFilesList);

            // 게임컨텐츠에 대한 교육자료 정보 가져오기
            List<EducationalResources> education = educationService.getEducation_togameno(gamecon.getGameContentNo());

            for (EducationalResources educationalResource : education) {
                ResourcesFiles resourcesFilesByResourceNo = resourcesFilesService.getFile(educationalResource.getResourceNo());
                educationalResource.setResourcesFilesList(resourcesFilesByResourceNo);
            }
            gamecon.setEducationalResourcesList(education);

        }
        int currentPage = gamePage.getPageable().getPageNumber();
        int totalPages = gamePage.getTotalPages();
        int pageRange = 5; // 한 번에 보여줄 페이지 범위

        int startPage = Math.max(0, currentPage - pageRange / 2);
        int endPage = startPage + pageRange - 1;
        if (endPage >= totalPages) {
            endPage = totalPages - 1;
            startPage = Math.max(0, endPage - pageRange + 1);
        }

        // 페이지 번호에 1을 더해줍니다.
        startPage += 1;
        endPage += 1;

        model.addAttribute("gamePage", gamePage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("searchText", searchText);
        return "game/list";
    }


    // 게임 상세조회
    @GetMapping("/detail/{gameContentNo}")
    public String gameDetail(@PathVariable Integer gameContentNo, Model model) {
        GameContents gameContents = gameService.getGameContents(gameContentNo);

        //3.Model
        model.addAttribute("gameContents", gameContents);
        return "game/detailForm";
    }


    //게임콘텐츠 수정폼
    @GetMapping("/modify/{gameContentNo}")
    public String gameModify(@PathVariable Integer gameContentNo, Model model,HttpSession session) {
        GameContents gameContents = gameService.getGameContents(gameContentNo);
        GameValidation gameValidation = gameService.getContentValidation(gameContents);    // Validation사용

        List<EducationalResources> educationalResources = new ArrayList<>();
        List<EducationalResources> allEducation = educationService.getDistinct();

        // 교육자료에 대한 파일 정보를 가져와서 모델에 추가
        for (EducationalResources education : allEducation) {
            ResourcesFiles resourcesFilesList = resourcesFilesService.getFile(education.getResourceNo());
            education.setResourcesFilesList(resourcesFilesList);
        }

        //게임컨텐츠값으로 선택된 EducationalResources정보를 가져온다.
        List<EducationalResources> selectEducation = educationService.getEducation_togameno(gameContents.getGameContentNo());

        session.setAttribute("educationalResources", educationalResources);
        model.addAttribute("allEducation", allEducation);
        model.addAttribute("gameValidation", gameValidation);
        model.addAttribute("selectEducation", selectEducation);

        return "game/modify";
    }


    @PostMapping("/modify/{gameContentNo}")
    public String modifyGame(@PathVariable Integer gameContentNo, @ModelAttribute("gameValidation") GameValidation gameValidation, @RequestParam MultiValueMap<String, String> params, @RequestParam(name = "selectedValues", required = false) String selectedValues) throws IOException {
        // 1. Delete Files
        List<String> deletedFiles = params.get("deletedFiles");
        if (deletedFiles != null && !deletedFiles.isEmpty() && "2".equals(deletedFiles.get(0))) {
            gameFilesService.deleteFile(gameContentNo);
        }

        // 2. Modify Game Contents
        GameContents gameContents = gameService.getGameContents(gameContentNo);
        if (gameContents != null) {
            // Perform game content modification here

            // 3. Process Additional Educational Resources
            if (selectedValues != null && !selectedValues.isEmpty()) {
                String[] selectedResourceNos = selectedValues.split(",");
                for (String resourceNoStr : selectedResourceNos) {
                    int resourceNo = Integer.parseInt(resourceNoStr);
                    EducationalResources education = educationService.getEducation(resourceNo);
                    if (education != null) {
                        if (education.getGameContents() == null) {
                            educationService.pgInsert(education, gameContents);
                        } else {
                            EducationValidation educationValidation = educationService.toEducationValidation(education);
                            educationService.copysave(educationValidation, gameContents);
                        }
                    }
                }
            }

            // 4. Save Uploaded File
            MultipartFile fileName = gameValidation.getBoardFile().get(0);
            if (fileName != null && !fileName.isEmpty()) {
                gameFilesService.save(gameContents, gameValidation);
            }

            return "redirect:/game/list2";
        }
        return "redirect:/game/list2"; // Handle the case where gameContents is not found
    }


    // 파일 다운로드 요청 처리
    @GetMapping("/downloadFile/{fileNo}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer fileNo) {
        // 파일을 서버에서 가져오는 로직을 구현하고 Resource 객체를 생성
        GameContentFiles fileByFileNo = gameFilesService.getFileByFileNo(fileNo);
        System.out.println("fileNo?"+fileNo);
        try {
            if (fileByFileNo != null) {
                return gameFilesService.downloadFile(fileByFileNo, fileByFileNo.getCopyFileName());
            }else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            // 파일을 읽을 수 없는 경우 에러 응답을 보냅니다.
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

/*
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
        gameFilesService.deleteFile(gameContents.getGameContentNo());
        gameService.delete(gameContents);
        return "redirect:/game/list2";    //목록으로이동
    }*/

    //  삭제하기
    @GetMapping("/test")
    public String test() {
        return "sb-admin-7.0.4/charts";    //목록으로이동
    }

    // ----------------- 테스트 --------------------!


 /*   // page 1
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

       *//* model.addAttribute("gameValidation",gameValidation);*//*
        model.addAttribute("allEducation", allEducation);
        return "game/page2";
    }*/

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

/*
    //page2
    @PostMapping("/page2")
    public String handlePage2Post(@ModelAttribute("gameValidation") @Valid GameValidation gameValidation, BindingResult bindingResult,
                                  HttpSession session, Model model) {
       // List<EducationalResources> educationalResources = (List<EducationalResources>) session.getAttribute("educationalResources");
        //List<String> originFileNames = (List<String>) session.getAttribute("gameContentFiles");
       // gameValidation = (GameValidation) session.getAttribute("gameValidation");


       // model.addAttribute("educationalResources", educationalResources);
       // model.addAttribute("originFileNames", originFileNames);
       // model.addAttribute("gameValidation", gameValidation);

        return "redirect:/game/page3"; // Page3로 리다이렉트
    }

// page3
    @GetMapping("/page3")
    public String getPage3(Model model,HttpSession session) {

        // 세션에서 정보를 가져옵니다.
       */
/* List<EducationalResources> educationalResources = (List<EducationalResources>) session.getAttribute("educationalResources");
        List<String> originFileNames = (List<String>) session.getAttribute("gameContentFiles");
        GameValidation gameValidation = (GameValidation) session.getAttribute("gameValidation");

        // 모델에 추가하여 뷰로 전달합니다.
        model.addAttribute("educationalResources", educationalResources);
        model.addAttribute("originFileNames", originFileNames);
        model.addAttribute("gameValidation", gameValidation);*//*


        return "game/page3";
    }
*/


    // 저장 처리 로직
    @PostMapping("/save")
    public String saveGame(@ModelAttribute("gameValidation") @Valid GameValidation gameValidation, BindingResult bindingResult, HttpSession session, @RequestParam("boardFile") MultipartFile file, Model model) throws IOException {
        // page3에서 확인 버튼을 눌렀을 때 서버에 저장
        // gameValidation 객체에 필요한 데이터가 모두 채워져 있을 것입니다.

        //정가, 할인율, 판매가가 null이면 보여주는 에러
       /* if(gameValidation.getOriginal_price() == null) {
            bindingResult.rejectValue("original_price", "original_priceInCorrect", "값을 확인해주세요.");
            return "game/page3";
        }*/

        //세션에서 값을 가져옴
        List<String> originFileNames = (List<String>) session.getAttribute("gameContentFiles");
        //세션에 저장된 educationalResources 값을 가져온다
        List<EducationalResources> educationalResources = (List<EducationalResources>) session.getAttribute("educationalResources");

        // 기존의 gameValidation 값을 가져옵니다.
        GameValidation gameValidationPage1 = (GameValidation) session.getAttribute("gameValidationPage1");

        // gameValidationPage1을 업데이트합니다.
        gameValidationPage1.setOriginal_price(gameValidation.getOriginal_price());
        gameValidationPage1.setDiscount_rate(gameValidation.getDiscount_rate());
        gameValidationPage1.setSale_price(gameValidation.getSale_price());

        // page3에서 입력 받은 데이터를 세션에 추가 하여 session으로 저장
        session.setAttribute("gameValidation", gameValidationPage1);

        System.out.println("gameValidation?: "+gameValidation);

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
/*

    //게임컨텐츠 구매 할때 목록조회
    @GetMapping("/alllist")
    public String gameList(Model model, @RequestParam(value = "page", defaultValue = "0") int page, GameContents gameContents) {

        // 게임컨텐츠 목록 조회
        Page<GameContents> gamePage = this.gameService.getList(page);

        for (GameContents gamecon : gamePage.getContent()) {
            // 게임컨텐츠에 대한 파일 정보 가져오기
            List<GameContentFiles> gameContentFilesList = gameFilesService.getGameFilesByGameContentNo(gamecon.getGameContentNo());
            gamecon.setGameContentFilesList(gameContentFilesList);

            // 게임컨텐츠에 대한 교육자료 정보 가져오기
            List<EducationalResources> education = educationService.getEducation_togameno(gamecon.getGameContentNo());

            for (EducationalResources educationalResource : education) {
                List<ResourcesFiles> resourcesFilesByResourceNo = resourcesFilesService.getResourcesFilesByResourceNo(educationalResource.getResourceNo());
                educationalResource.setResourcesFilesList(resourcesFilesByResourceNo);
            }
            gamecon.setEducationalResourcesList(education);

        }
        model.addAttribute("gamePage", gamePage);

        return "cyborg-1.0.02/streams";
    }
*/

    //게임컨텐츠 구매 할때 목록조회 (페이지네이션)
    @GetMapping("/alllist")
    public String gameList(Model model, @RequestParam(value = "page", defaultValue = "0") int page, GameContents gameContents,@RequestParam(required = false, defaultValue = "") String searchText,@RequestParam(required = false, defaultValue = "") List<Integer> gameContentNo) {

        // 게임컨텐츠 목록 조회
        Page<GameContents> gamePage = this.gameService.getallList(searchText, searchText, page);

        for (GameContents gamecon : gamePage.getContent()) {
            // 게임컨텐츠에 대한 파일 정보 가져오기
            List<GameContentFiles> gameContentFilesList = gameFilesService.getGameFilesByGameContentNo(gamecon.getGameContentNo());
            gamecon.setGameContentFilesList(gameContentFilesList);

            // 게임컨텐츠에 대한 교육자료 정보 가져오기
            List<EducationalResources> education = educationService.getEducation_togameno(gamecon.getGameContentNo());

            for (EducationalResources educationalResource : education) {
                ResourcesFiles resourcesFilesByResourceNo = resourcesFilesService.getFile(educationalResource.getResourceNo());
                educationalResource.setResourcesFilesList(resourcesFilesByResourceNo);
            }
            gamecon.setEducationalResourcesList(education);

        }
        int currentPage = gamePage.getPageable().getPageNumber();
        int totalPages = gamePage.getTotalPages();
        int pageRange = 5; // 한 번에 보여줄 페이지 범위

        int startPage = Math.max(0, currentPage - pageRange / 2);
        int endPage = startPage + pageRange - 1;
        if (endPage >= totalPages) {
            endPage = totalPages - 1;
            startPage = Math.max(0, endPage - pageRange + 1);
        }

// 페이지 번호에 1을 더해줍니다.
        startPage += 1;
        endPage += 1;

        model.addAttribute("gamePage", gamePage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("searchText", searchText);

        return "cyborg-1.0.02/streams";
    }

    @PostMapping("/saveSelectedValues")
    public ResponseEntity<Map<String, Double>> saveSelectedValues(@RequestBody Map<String, String> requestBody, HttpSession session) {
        String selectedValuesString = requestBody.get("selectedValues");
        String[] selectedValues = selectedValuesString.split(",");

        Map<String, Double> salePrices = new HashMap<>();
        List<Integer> selectedValueList = new ArrayList<>();

        // 세션에서 선택한 값을 가져옴
        List<Integer> sessionSelectedValues = (List<Integer>) session.getAttribute("selectedValues");

        if (sessionSelectedValues == null) {
            sessionSelectedValues = new ArrayList<>();
        }

        for (String selectedValue : selectedValues) {
            int gameContentId = Integer.parseInt(selectedValue);

            // 만약 해당 ID가 이미 선택되어 있는 경우, 이를 제외하고 세션에서도 제거
            if (sessionSelectedValues.contains(gameContentId)) {
                sessionSelectedValues.remove(Integer.valueOf(gameContentId));
            } else {
                GameContents gameContents = gameService.getGameContents(gameContentId);
                if (gameContents != null) {
                    Double salePrice = gameContents.getSalePrice();
                    salePrices.put("salePrice" + gameContentId, salePrice);
                    selectedValueList.add(gameContentId);
                }
            }
        }

        // 세션에 선택한 값을 업데이트
        session.setAttribute("selectedValues", selectedValueList);

        return ResponseEntity.ok(salePrices);
    }




//게임컨텐츠 구매 할때 목록조회
    @GetMapping("/testlist")
    public String test(Model model, @RequestParam(value = "page", defaultValue = "0") int page, GameContents gameContents) {

        return "cyborg-1.0.02/streams";
    }




}//class
