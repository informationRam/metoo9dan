package com.idukbaduk.metoo9dan.member.controller;

import com.idukbaduk.metoo9dan.member.service.MailService;
import com.idukbaduk.metoo9dan.member.service.SmsService;
import com.idukbaduk.metoo9dan.member.dto.SmsResponseDTO;
import com.idukbaduk.metoo9dan.member.dto.VerificationRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Map;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class VerfificationController {

    private final MailService mailService;
    private final SmsService smsService;
    //secureRandom으로 보안성 강화 인증번호 생성
    private final SecureRandom secureRandom = new SecureRandom();
    private final String numericCharacters = "0123456789";

    //이메일 발송
    @PostMapping("/sendEmailVerification")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        System.out.println("이메일발송 컨트롤러");
        String email = request.get("valiEmail");
        System.out.println("email:"+email);
        if (mailService.sendEmailCode(email)) {
            return ResponseEntity.ok().body("{\"success\": true}");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"success\": false}");
        }
    }

    @PostMapping("/verifyEmailCode")
    public ResponseEntity<?> verifyEmailCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String inputCode = request.get("emailCode");
        if (mailService.verifyEmailCode(email, inputCode)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증번호를 다시 확인하세요");
        }
    }

    // 휴대폰 인증번호 발송
    @PostMapping("/sendSMS-code")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(@RequestBody VerificationRequest request, HttpSession session) {
        try {
            String memName =  request.getMemName();
            String to = request.getTo();
            // 인증 코드 생성
            String verificationCode = generateVerificationCode();

            // 로깅 추가
            System.out.println("Received memName: " + memName);
            System.out.println("Received phone: " + to);
            System.out.println("Received code: " + verificationCode);

            // SMS 서비스를 통해 인증 코드 발송
            SmsResponseDTO response = smsService.sendSms(to, verificationCode);

            if (response != null && "202" .equals(response.getStatusCode())) {
                // 성공 상태 코드가 202인 경우에 성공, 인증정보 저장 및 타임 아웃 설정
                session.setAttribute("verificationCode", verificationCode);
                session.setAttribute("memName", memName);
                session.setAttribute("to", to);

                // 1분(60초) 동안만 세션 유지
                session.setMaxInactiveInterval(60); // 60초 설정

                // 인증 코드 발송에 성공한 경우
                return ResponseEntity.ok(Map.of("success", true, "message", "인증 코드가 발송되었습니다."));
            } else {
                // 인증 코드 발송에 실패한 경우
                return ResponseEntity.ok(Map.of("success", false, "message", "인증 코드 발송에 실패했습니다."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false, "message", "인증 코드 발송에 실패했습니다."));
        }
    }
    @PostMapping("/verifyCode")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody VerificationRequest request, HttpSession session) {
        try {
            // 세션에서 저장된 인증 정보 가져오기
            String savedVerificationCode = (String) session.getAttribute("verificationCode");
            String memName = (String) session.getAttribute("memName");
            String to = (String) session.getAttribute("to");

            if (savedVerificationCode != null && savedVerificationCode.equals(request.getVerificationCode())) {
                // 인증에 성공한 경우
                session.removeAttribute(savedVerificationCode); //성공하면 삭제
                return ResponseEntity.ok(Map.of("success", true, "message", "본인 인증이 완료되었습니다.", "userName", memName, "userPhone", to));
            } else {
                // 인증에 실패한 경우
                return ResponseEntity.ok(Map.of("success", false, "message", "인증에 실패했습니다."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false, "message", "인증에 실패했습니다."));
        }
    }

    // 난수 생성 로직 구현
    private String generateVerificationCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(numericCharacters.length());
            sb.append(numericCharacters.charAt(randomIndex));
        }
        return sb.toString();
    }



}