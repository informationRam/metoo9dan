package com.idukbaduk.metoo9dan.payments.kakaopay;

import com.idukbaduk.metoo9dan.common.entity.*;
import com.idukbaduk.metoo9dan.education.service.EducationService;
import com.idukbaduk.metoo9dan.education.vaildation.EducationValidation;
import com.idukbaduk.metoo9dan.game.service.GameFilesService;
import com.idukbaduk.metoo9dan.game.service.GameService;
import com.idukbaduk.metoo9dan.game.vaildation.GameValidation;
import com.idukbaduk.metoo9dan.member.service.MemberService;
import com.idukbaduk.metoo9dan.payments.service.PaymentsService;
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
import java.security.Principal;
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
    private final MemberService memberService;
    /**
     * 결제요청
     */
    @PostMapping("/ready")
    public KakaoReadyResponse readyToKakaoPay(@RequestParam("item_name") String itemName, @RequestParam("total_amount") int totalAmount
    ,Principal principal) {

            Member member = memberService.getUser(principal.getName());

            String totalAmount2 = String.valueOf(totalAmount);
            return kakaoPayService.kakaoPayReady(itemName,totalAmount2,member.getMemberId());
    }


}//class
