package com.idukbaduk.metoo9dan.admin.controller;

import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.idukbaduk.metoo9dan.common.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;


@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminController {

    private final MemberService memberService;


    //회원관리페이지 보여주기
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value="/listMember")
    public String showMemberList(Model model) throws Exception {
        List<Member> members = memberService.getAllMembers(); // 모든 회원의 목록

        // joinDate를 원하는 형식의 문자열로 변환
        List<String> formattedJoinDates = new ArrayList<>();
        for (Member member : members) {
            String formattedDate = DateTimeUtils.formatLocalDateTime(member.getJoinDate(), "yyyy.MM.dd");
            formattedJoinDates.add(formattedDate);
        }

        model.addAttribute("members", members);
        model.addAttribute("joinDate", formattedJoinDates);
        return "admin/memberList";
    }



}