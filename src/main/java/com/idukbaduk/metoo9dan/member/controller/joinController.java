package com.idukbaduk.metoo9dan.member.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.idukbaduk.metoo9dan.member.SmsService;
import com.idukbaduk.metoo9dan.member.dto.MessageDTO;
import com.idukbaduk.metoo9dan.member.dto.SmsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import java.security.InvalidKeyException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

@RequestMapping("/join")
@RequiredArgsConstructor
@Controller

public class joinController {

    private final SmsService smsService;

    //SMS 발송 뷰페이지 반환
    @GetMapping("/sms")
    public String getSmsPage() {

        return "/member/sendSms";
    }
    
    //SMS 발송 결과 뷰페이지 반환
    @PostMapping("/sms/send")
    public SmsResponseDTO sendSms(@RequestBody MessageDTO messageDto) throws  UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        SmsResponseDTO responseDto  = smsService.sendSms(messageDto);
        model.addAttribute("response", response);
        return "/member/smsResult";
    }


}
