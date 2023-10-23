package com.idukbaduk.metoo9dan.payments.controller;

import com.idukbaduk.metoo9dan.payments.kakaopay.KakaoApproveResponse;
import com.idukbaduk.metoo9dan.payments.kakaopay.KakaoPayService;
import com.idukbaduk.metoo9dan.payments.kakaopay.KakaoReadyResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Controller
class PaymentsControllerTest {


    @Autowired
    private KakaoPayService kakaoPayService;
    @Test
    public KakaoReadyResponse readyToKakaoPay() {

        return kakaoPayService.kakaoPayReady();
    }

    /**
     * 결제 성공
     *
     *
     t*/
    @Test
    @GetMapping("/success")
    public ResponseEntity afterPayRequest(@RequestParam("pg_token") String pgToken) {

        KakaoApproveResponse kakaoApprove = kakaoPayService.approveResponse(pgToken);
        return new ResponseEntity<>(kakaoApprove, HttpStatus.OK);
    }


}