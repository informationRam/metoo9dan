package com.idukbaduk.metoo9dan.member.controller;


import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.dto.EducatorInfoDTO;
import com.idukbaduk.metoo9dan.member.dto.MemberDTO;
import com.idukbaduk.metoo9dan.member.service.MemberService;
import com.idukbaduk.metoo9dan.member.validation.UserCreateForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/member")
@RequiredArgsConstructor
@Controller

public class memberController {

    private final MemberService memberService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //test- 마에페이지 접근
    @GetMapping(value="/mypage")
    public String myPage() throws Exception {
        return "member/myPage";
    }

    //로그인  폼 보여주기
    @GetMapping("/login")
    public String login(Model model, UserCreateForm userCreateForm){
        model.addAttribute("userCreateForm", userCreateForm);
        return "/member/signupForm2";
    }

    //회원가입 폼 보여주기
    @GetMapping("/join")
    public String joinForm(UserCreateForm userCreateForm, Model model){
        // userCreateForm을 모델에 추가
        model.addAttribute("userCreateForm", userCreateForm);
        return "/member/signupForm2";
    }

    //회원가입 처리하기
    //사용자 정보를 memberDTO에 받고,이를 member Entity에 옮기고 DB에 저장한다.
    @PostMapping("/join")
    public String createUser(UserCreateForm form, Model model) {
        ModelMapper modelMapper = new ModelMapper();
        System.out.println("join컨트롤러 진입");
        MemberDTO memberDTO = modelMapper.map(form, MemberDTO.class);
        memberDTO.setPassword(passwordEncoder.encode(form.getPwd1()));

        if ("EDUCATOR".equals(form.getRole())) {
            EducatorInfoDTO educatorInfoDTO = modelMapper.map(form, EducatorInfoDTO.class);

            // MemberDTO 데이터를 Member 엔티티로 이동
            Member member = modelMapper.map(memberDTO, Member.class);

            // EducatorInfoDTO 데이터를 EducatorInfo 엔티티로 이동
            EducatorInfo educatorInfo = modelMapper.map(educatorInfoDTO, EducatorInfo.class);

            memberService.createUserWithEducatorInfo(member, educatorInfo);
        } else {
            // MemberDTO 데이터를 Member 엔티티로 이동
            Member member = modelMapper.map(memberDTO, Member.class);

            memberService.createUser(member);
        }

        return "redirect:/";
    }


//
//    //SMS 발송 처리 및  뷰페이지 반환
//    @PostMapping("/sms/send")
//    public String sendSmsWithRandomContent(@RequestParam("to") String to, Model model) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
//        SmsResponseDTO response = smsService.sendSms(to);
//        model.addAttribute("response", response);
//        return "/member/smsResult";
//    }



}
