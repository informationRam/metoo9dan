package com.idukbaduk.metoo9dan.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/admin")
@Controller
public class adminController {

    //회원관리페이지 보여주기
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value="/listMember")
    public String memberManange() throws Exception {

        return "/member/memberManage";
    }
}