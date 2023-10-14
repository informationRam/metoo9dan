package com.idukbaduk.metoo9dan.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@Controller
public class adminController {

    //회원관리페이지 보여주기
    @GetMapping(value="/memberManage")
    public String memberManange() throws Exception {

        return "/member/memberManage";
    }
}