package com.idukbaduk.metoo9dan.member.controller;


import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.mail.service.EmailService;
import com.idukbaduk.metoo9dan.member.dto.EducatorInfoDTO;
import com.idukbaduk.metoo9dan.member.dto.MemberDTO;
import com.idukbaduk.metoo9dan.member.service.MemberService;
import com.idukbaduk.metoo9dan.member.validation.LoginVaildation;
import com.idukbaduk.metoo9dan.member.validation.UserCreateForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/member")
@RequiredArgsConstructor
@Controller

public class memberController {

    private final MemberService memberService;
    // 메일발송 서비스
    private final EmailService emailService;
    private PasswordEncoder passwordEncoder;
//    @Autowired
//    private PasswordEncoder passwordEncoder;

    //test- 마에페이지 접근
    @GetMapping(value="/mypage")
    public String myPage() {
        return "member/myPage";
    }

    //로그인 화면 보여주기
    @GetMapping("/login")
    public String login(Model model, LoginVaildation loginVaildation, UserCreateForm userCreateForm){
        model.addAttribute("userCreateForm", userCreateForm);
        return "/member/loginForm";
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
    public String createUser(@Valid UserCreateForm form, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "/user/signupForm2";
        }

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


    //id&pw 찾기 화면
    @GetMapping("/idpwSearch")
    public String searchidform() {

        return "/member/forgotAccount";
    }

    //id 찾기 GET 요청
//    @GetMapping("/searchId")
//    @ResponseBody
//    public Map<String, String> getsearchid(@RequestParam("email") String email) {
//        Map<String, String> member = new HashMap<>();
//        String memberId = memberService.searchId(email);
//        //회원정보에 사용자가 입력한 이메일이 있는지 확인
//        if (!memberService.checkEmailDuplication(email) || memberId == null) {
//            member.put("memberId", "회원정보를 찾을 수 없습니다.");
//            return member;
//        } else {
//            member.put("memberId", memberId);
//            return member;
//        }
//    }
    //id 찾기 POST 요청
    @PostMapping("/idSearch")
    @ResponseBody
    public Map<String, String> SearchId(@RequestParam("email") String email) {
        Map<String, String> member = new HashMap<>();
        String memberId = memberService.searchId(email);
        //회원정보에 사용자가 입력한 이메일이 있는지 확인
        if (!memberService.checkEmailDuplication(email) || memberId == null) {
            member.put("memberId", "회원정보를 찾을 수 없습니다.");
            return member;
        } else {
            member.put("memberId", memberId);
            return member;
        }
    }
    // 비밀번호 찾기
    @PostMapping("/pwdSearch")
    @ResponseBody
    public Map<String, String> pwdSearch(@RequestParam("email") String email, @RequestParam("memberId") String memberId) {
        System.out.println("email" + email);
        System.out.println("memberId" + memberId);
        Map<String, String> message = new HashMap<>();

        // 이메일 중복 여부 체크
        if (memberService.searchPwd(memberId, email)) {
            message.put("message", "사용자 정보를 찾을 수 없습니다.");
            return message;
        } else {
            String tempPassword = emailService.sendSimpleMessage(email);   // 메일 발송 후 임시 비밀번호 값 저장
            Member member = memberService.getUser(memberId);
            memberService.userPwdModify(member, tempPassword);                // 임시 패스워드로 변경
            message.put("message", "메일을 발송하였습니다.");
            return message; // 메일 전송 성공 -> 로그인 창으로 이동
        }
    }

}
