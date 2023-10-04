package com.idukbaduk.metoo9dan.member.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.idukbaduk.metoo9dan.member.SmsService;
import com.idukbaduk.metoo9dan.member.dto.SmsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import java.security.InvalidKeyException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

@RequestMapping("/member")
@RequiredArgsConstructor
@Controller

public class memberController {

    private final SmsService smsService;


    //로그인 폼을 보여주기
    @GetMapping("/login")
    public String login(){
        return "/member/loginPage";
    }

    //회원가입 폼을 보여줘
    @GetMapping("/signup2")
    public String signUp2(){
        return "/member/signupForm2";
    }

    //회원가입 폼을 보여줘
    @GetMapping("/signup")
    public String signUp(){
        return "/member/signupForm";
    }

    //SMS 발송 뷰페이지
    @GetMapping("/sms")
    public String getSmsPage() {
        return "/member/sendSms";
    }
    
    //SMS 발송 결과 뷰페이지 반환
    @PostMapping("/sms/send")
    public String sendSmsWithRandomContent(@RequestParam("to") String to, Model model) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        SmsResponseDTO response = smsService.sendSms(to);
        model.addAttribute("response", response);
        return "/member/smsResult";
    }



}
