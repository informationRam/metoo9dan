package com.idukbaduk.metoo9dan.member.service;

import com.idukbaduk.metoo9dan.mail.dto.MailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final HttpSession httpSession; // 세션 객체를 주입
    private final SecureRandom secureRandom = new SecureRandom();
    private final String numericCharacters = "0123456789";

    // 인증번호 난수 생성
    private String createCode() {
        StringBuilder emailCode = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(numericCharacters.length());
            emailCode.append(numericCharacters.charAt(randomIndex));
        }
        return emailCode.toString();
    }

    public boolean sendEmailCode(String valiEmail) { //valiEmail은 컨트롤러에서 넘겨준 email의 값
        //2.MailDTO.content 영역에 6자리 난수를 생성해 저장
        String mailCode = createCode();
        httpSession.setAttribute(valiEmail, mailCode); // 생성된 코드를 세션에 저장
        System.out.println("문자발송은 여기로 " + valiEmail + ": " + mailCode); // 이 부분을 추가
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("yujinahn95@gmail.com");
            mailMessage.setTo(valiEmail);
            mailMessage.setSubject("나도9단 본인 인증 확인 코드");
            mailMessage.setText("당신의 인증 코드는 " + mailCode + " 입니다.");

            javaMailSender.send(mailMessage);

            return true;
        } catch (MailException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 사용자가 입력한 인증 코드 검증
    public boolean verifyEmailCode(String valiEmail, String inputCode) {
        System.out.println("인증서비스 진입");
        Object codeInSession = httpSession.getAttribute(valiEmail); //valiEmail로 받은 인증번호 저장
        System.out.println("codeInSession이 뭔데:" + codeInSession);
        System.out.println("input코드는뭔데:"+inputCode.equals(codeInSession.toString()));

        //세션저장 코드 = 사용자 입력코드
        if (codeInSession != null && inputCode.equals(codeInSession.toString())) {
            httpSession.removeAttribute(valiEmail); // 성공한 경우 세션에서 인증 코드 제거
            System.out.println("인증성공!");
            return true;
        }
        return false;
    }
}