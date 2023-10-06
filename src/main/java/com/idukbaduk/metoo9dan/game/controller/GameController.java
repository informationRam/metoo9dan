package com.idukbaduk.metoo9dan.game.controller;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import com.idukbaduk.metoo9dan.game.service.GameFilesService;
import com.idukbaduk.metoo9dan.game.service.GameService;
import com.idukbaduk.metoo9dan.game.vaildation.GameVaildation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final GameFilesService gameFilesService;

    //게임컨텐츠 등록 폼
    @GetMapping("/addForm")
    public String gameAddForm(GameVaildation gameVaildation, Model model) {
        model.addAttribute("gameVaildation", gameVaildation);
        return "game/addForm";
    }

    //게임 컨텐츠 등록 처리
    @PostMapping("/add")
    public String gameAdd(@ModelAttribute("gameVaildation") @Valid GameVaildation gameVaildation, @RequestParam("boardFile") MultipartFile file, BindingResult bindingResult, Model model) throws IOException {
        System.out.println("정가 : "+gameVaildation.getOriginal_price());
        System.out.println("판매가 : "+gameVaildation.getSale_price());

        // 업로드된 파일의 확장자 확인
        MultipartFile fileName = gameVaildation.getBoardFile().get(0);

        if (fileName == null || fileName.isEmpty()) {
                gameVaildation.setBoardFile(null);
        }

        if (bindingResult.hasErrors()) {
            return "game/addForm";
        } else{
            gameService.save(gameVaildation);
            return "redirect:/game/list";
        }
    }

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

    // 게임콘텐츠 수정폼
    @GetMapping("/modify/{gameContentNo}")
    public String gameModify(@PathVariable Integer gameContentNo, Model model) {
        GameContents gameContents = gameService.getGameContents(gameContentNo);
        GameVaildation gameVaildation = gameService.getContentVaildation(gameContents);

        model.addAttribute("gameVaildation", gameVaildation);
        return "game/detailForm";
    }


    //  삭제하기
   /* @GetMapping("/delete/{gameContentNo}")
    public String gameDelete(@PathVariable("gameContentNo") Integer gameContentNo) {
*/
        // 현재 사용자의 인증 정보를 가져옴
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 현재 사용자의 권한 중 하나라도 "ROLE_ADMIN"이 아니라면
//        if (!authentication.getAuthorities().stream()
//                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"권한이 없습니다.");
//        }
      /*  GameContents gameContents = gameService.getGameContents(gameContentNo);
        gameService.delete(gameContents);*/
      /*  return "redirect:/game/list";    //목록으로이동
    }*/


    // 계산 Ajax
  /*  @ResponseBody
    @PostMapping("/calculator")
    public Map<String, String> calculator(@RequestParam("email") String email) {
        Map<String, String> user = new HashMap<>();
        String userid = userService.searchId(email);
        //회원정보에 사용자가 입력한 이메일이 있는지 확인
        if (!userService.checkEmailDuplication(email) || userid == null) {
            user.put("userid", "회원정보를 찾을 수 없습니다.");
            return user;
        } else {
            user.put("userid", userid);
            return user;
        }
    }*/

}//class
