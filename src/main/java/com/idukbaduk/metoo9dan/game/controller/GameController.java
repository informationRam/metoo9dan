package com.idukbaduk.metoo9dan.game.controller;

import com.idukbaduk.metoo9dan.game.service.GameService;
import com.idukbaduk.metoo9dan.game.vaildation.GameVaildation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    //게임컨텐츠 등록 폼
    @GetMapping("/addForm")
    public String gameAddForm(GameVaildation gameVaildation, Model model) {
        model.addAttribute("gameVaildation", gameVaildation);
        return "game/gameAddForm";
    }

    //게임 컨텐츠 등록 처리
    @PostMapping("/add")
    public String gameAdd(@Valid GameVaildation gameVaildation, BindingResult bindingResult, Model model) {

        // originalFileName이 null이면 null을 반환하도록 추가
        if (gameVaildation.getOrigin_file_name() == null) {
            return null;
        }

        if (bindingResult.hasErrors()) {
            return "game/gameAddForm";
        } else if(gameVaildation.getOrigin_file_name() == null){
            gameVaildation.setOrigin_file_name(null);
            gameService.gameAdd(gameVaildation);
            return "redirect:/game/list";
        }else {
            gameService.gameAdd(gameVaildation);
            return "redirect:/game/list";
        }
    }

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
