package com.idukbaduk.metoo9dan.member.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.SmsService;
import com.idukbaduk.metoo9dan.member.dto.EducatorInfoDTO;
import com.idukbaduk.metoo9dan.member.dto.MemberDTO;
import com.idukbaduk.metoo9dan.member.dto.SmsRequestDTO;
import com.idukbaduk.metoo9dan.member.dto.SmsResponseDTO;
import com.idukbaduk.metoo9dan.member.service.MemberService;
import com.idukbaduk.metoo9dan.member.validation.UserCreateForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.InvalidKeyException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;

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

//    //로그인&회원가입 폼 보여주기
//    @GetMapping("/login")
//    public String login(){
//        return "/member/signupForm2";
//    }

    //회원가입 폼
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


//    //인증번호 발송
//    @PostMapping("/sendSMS-code")
//    @ResponseBody
//    public ResponseEntity<String> sendVerificationCode(@RequestBody Map<String, String> requestPayload,
//                                                       HttpSession session) {
//
//        try {
//            String memName = requestPayload.get("memName");
//            String to = requestPayload.get("to");
//            // 로깅 추가
//            System.out.println("Received memName: " + memName);
//            System.out.println("Received phone: " + to);
//            // 인증 코드 생성 및 발송
//            String verificationCode = generateVerificationCode(); // 인증 코드 생성 메서드 구현 필요
//            SmsResponseDTO response = smsService.sendSms(to, verificationCode);
//
//            if (response != null && "202" .equals(response.getStatusCode())) {
//                // 성공 상태 코드가 202인 경우에 성공으로 처리
//                session.setAttribute("verificationCode", verificationCode);
//                session.setAttribute("userName", memName); // name 값을 세션에 저장
//                session.setAttribute("userPhone", to); // to 값을 세션에 저장 (휴대폰 번호)
//                return ResponseEntity.ok("SMS 발송 성공");
//            } else {
//                // 그 외의 경우에는 실패로 처리
//                return ResponseEntity.badRequest().body("SMS 발송 실패");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
//        }
//    }

//    // 난수 생성 로직 구현
//    private String generateVerificationCode() {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < 6; i++) {
//            int randomIndex = secureRandom.nextInt(numericCharacters.length());
//            sb.append(numericCharacters.charAt(randomIndex));
//        }
//        return sb.toString();
//    }

//    // 본인 인증 코드 확인
//    @PostMapping("/verifyCode")
//    @ResponseBody
//    public ResponseEntity<String> verifyCode(@RequestBody Map<String, String> requestBody, HttpSession session) {
//        try {
//            String clientVerificationCode = requestBody.get("verificationCode");
//            String sessionVerificationCode = (String) session.getAttribute("verificationCode");
//
//            if (clientVerificationCode != null && clientVerificationCode.equals(sessionVerificationCode)) {
//                // 클라이언트가 입력한 인증 코드와 세션에 저장된 코드가 일치하는 경우
//                // 이후 로직을 추가하세요. (본인 인증 성공 및 회원 가입 등)
//                return ResponseEntity.ok("본인 인증 성공");
//            } else {
//                // 클라이언트가 입력한 인증 코드와 세션에 저장된 코드가 일치하지 않는 경우
//                return ResponseEntity.badRequest().body("본인 인증 실패");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
//        }
//    }

//            @PostMapping("/verifyCode")
//    public String verifySmsCode(@RequestParam("verificationCode") String verificationCode, HttpSession session,
//                                RedirectAttributes redirectAttributes) {
//        // 세션에서 저장된 인증 코드 가져오기
//        String storedVerificationCode = (String) session.getAttribute("verificationCode");
//
//        if (storedVerificationCode != null && storedVerificationCode.equals(verificationCode)) {
//            // 검증 성공 시 홈 페이지로 리다이렉트 또는 다음 작업을 수행
//            return "redirect:/home";
//        } else {
//            // 검증 실패 시 다시 SMS 인증 페이지로 이동
//            redirectAttributes.addFlashAttribute("error", "Invalid SMS code");
//            return "redirect:/sms-verification";
//        }
//    }



//
//    //SMS 발송 처리 및  뷰페이지 반환
//    @PostMapping("/sms/send")
//    public String sendSmsWithRandomContent(@RequestParam("to") String to, Model model) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
//        SmsResponseDTO response = smsService.sendSms(to);
//        model.addAttribute("response", response);
//        return "/member/smsResult";
//    }



}
