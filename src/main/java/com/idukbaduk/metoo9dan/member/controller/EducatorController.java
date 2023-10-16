package com.idukbaduk.metoo9dan.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/edu")
@Controller
public class EducatorController {

    //test- 학생관리 페이지 접근
    @GetMapping(value="/studentManage")
    public String studentManage() throws Exception {
        return "/member/studentManage";
    }
}
