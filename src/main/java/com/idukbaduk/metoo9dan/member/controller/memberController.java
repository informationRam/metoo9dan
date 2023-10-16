package com.idukbaduk.metoo9dan.member.controller;


import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.dto.EducatorInfoDTO;
import com.idukbaduk.metoo9dan.member.dto.MemberDTO;
import com.idukbaduk.metoo9dan.member.service.MemberService;
import com.idukbaduk.metoo9dan.member.validation.LoginValidation;
import com.idukbaduk.metoo9dan.member.validation.UserCreateForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/member")
@RequiredArgsConstructor
@Controller

public class memberController {

    private final MemberService memberService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //test- 마이페이지 접근
    @GetMapping(value="/mypage/{memberId}")
    public String myPage(  @PathVariable("memberId") String memberId,
                           Model model, Principal principal) throws Exception {
        Member member = memberService.getUser(principal.getName());
        String memname = member.getName(); //  회원이름
        String Id = member.getMemberId(); // 회원아이디
        String Role = member.getRole(); // 역할


        System.out.println("이름"+memname);
        System.out.println("아이디"+Id);

        model.addAttribute("member",member); // 로그인 사용자 전체 정보
        model.addAttribute("role",Role); // 로그인 사용자 전체 정보
        model.addAttribute("memberName",memname); // 로그인 회원 이름
        return "/member/myPage";
    }

    //로그인 & 회원가입 폼 화면 보여주기
    @GetMapping("/login")
    public String login(Model model, LoginValidation loginValidation, UserCreateForm userCreateForm){
        model.addAttribute("loginValidation", loginValidation);
        model.addAttribute("userCreateForm", userCreateForm);
        return "member/signupForm2";
    }


    //회원가입 처리하기
    //사용자 정보를 memberDTO에 받고,이를 member Entity에 옮기고 DB에 저장한다.
    @PostMapping("/join")
    public String createUser(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, Model model) {

        // userCreateForm 검증 실패시 다시 입력폼
        if (bindingResult.hasErrors()) {
            return "member/signupForm2";
        }

        ModelMapper modelMapper = new ModelMapper();
        System.out.println("join컨트롤러 진입");
        MemberDTO memberDTO = modelMapper.map(userCreateForm, MemberDTO.class);
        memberDTO.setPassword(passwordEncoder.encode(userCreateForm.getPwd1()));

        //비밀번호 일쳐여부 검사
        if (!userCreateForm.getPwd1().equals(userCreateForm.getPwd2())) {
            model.addAttribute("passwordMismatch", true);
            return "member/signupForm2";
        }
        //아이디 중복 검사
        if (memberService.checkmemberIdDuplication(userCreateForm.getMemberId())) {
            model.addAttribute("memberIdDuplicate", true);
            return "member/signupForm2";
        }
        //이메일 중복 검사
        if (memberService.checkEmailDuplication(userCreateForm.getEmail())) {
            model.addAttribute("emailDuplicate", true);
            return "member/signupForm2";
        }

        if ("EDUCATOR".equals(userCreateForm.getRole())) {
            // MemberDTO 데이터를 Member 엔티티로 이동
            Member member = modelMapper.map(memberDTO, Member.class);

            // EducatorInfoDTO 데이터를 EducatorInfo 엔티티로 이동
            EducatorInfoDTO educatorInfoDTO = modelMapper.map(userCreateForm, EducatorInfoDTO.class);
            EducatorInfo educatorInfo = modelMapper.map(educatorInfoDTO, EducatorInfo.class);

            memberService.createUserWithEducatorInfo(member, educatorInfo);
        } else {
            // MemberDTO 데이터를 Member 엔티티로 이동
            Member member = modelMapper.map(memberDTO, Member.class);
            memberService.createUser(member);
        }

        return "redirect:/member/login"; //회원가입 후 로그인 페이지
    }

    //id&pw 찾기 화면
    @GetMapping("/idpwSearch")
    public String searchidform() {

        return "/member/forgotAccount";

    }

}
