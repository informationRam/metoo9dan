package com.idukbaduk.metoo9dan.payments.kakaopay;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/kakaopay")
@RequiredArgsConstructor
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;
    /**
     * 결제요청
     */
    @PostMapping("/ready")
    public KakaoReadyResponse readyToKakaoPay(@RequestParam("item_name") String itemName, @RequestParam("total_amount") int totalAmount
    ) {
        // 이제 itemName과 totalAmount 변수에 사용자가 입력한 값이 들어있습니다.
        // 여기에서 카카오페이 결제 요청을 처리할 수 있습니다.

        // 예를 들어, itemName과 totalAmount를 로깅해 봅니다.
        System.out.println("상품명: " + itemName);
        System.out.println("총 금액: " + totalAmount);
        String totalAmount2 = String.valueOf(totalAmount);
        return kakaoPayService.kakaoPayReady(itemName,totalAmount2);
    }

    /**
     * 결제 성공
     */
/*
    @GetMapping("/success")
    public ResponseEntity afterPayRequest(@RequestParam("pg_token") String pgToken) {

        System.out.println("afterPayRequest?" +pgToken);
        KakaoApproveResponse kakaoApprove = kakaoPayService.approveResponse(pgToken);

        return new ResponseEntity<>(kakaoApprove, HttpStatus.OK);
    }
*/


    @GetMapping("/success")
    public String afterPayRequest(@RequestParam("pg_token") String pgToken, Model model) {
        System.out.println("afterPayRequest? " + pgToken);
        KakaoApproveResponse kakaoApprove = kakaoPayService.approveResponse(pgToken);

        // 여기에서 game/list.html로 리다이렉트
        return "redirect:/game/list";
    }




}//class
