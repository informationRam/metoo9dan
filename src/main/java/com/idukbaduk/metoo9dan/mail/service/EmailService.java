package com.idukbaduk.metoo9dan.mail.service;

import  com.idukbaduk.metoo9dan.mail.DTO.MailDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public String sendSimpleMessage(String mailAddress) {
        System.out.println("sendSimpleMessage진입");
        SimpleMailMessage message = new SimpleMailMessage();
        MailDTO mailDto = new MailDTO();
        String str = getTempPassword();

        mailDto.setTo("ttangppy@gmail.com");
        mailDto.setTitle("[나도9단] 임시비밀번호 메일 발송 안내");
        mailDto.setContent("임시비밀번호 안내 [" + str + "] 로그인 > 마이페이지 > 내 정보수정을 통해 패스워드를 변경해주세요");
        mailDto.setAddress(mailAddress);

        message.setFrom(mailDto.getTo());
        message.setTo(mailDto.getAddress());
        message.setSubject(mailDto.getTitle());
        message.setText(mailDto.getContent());
        emailSender.send(message);
        return str;
    }

    // 임시 패스워드
    public String getTempPassword() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        String str = "";

        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }


    //랜덤ID생성 K-000 (000은 숫자랜덤임)
    public String generateRandomID() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder randomID = new StringBuilder("K-");

        Random random = new Random();

        // Generate a random three-digit number
        int randomNumber = random.nextInt(900) + 100; // This generates a number between 100 and 999

        randomID.append(randomNumber);

        return randomID.toString();
    }

}