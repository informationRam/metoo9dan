package com.idukbaduk.metoo9dan.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.idukbaduk.metoo9dan.member.SmsService;
import com.idukbaduk.metoo9dan.member.repository.EducatorinfoRepository;
import com.idukbaduk.metoo9dan.member.repository.MemberRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

//@RequiredArgsConstructor
//@Service
//public class MemberService {
//    private final MemberRepository memberRepository;
//    private final EducatorinfoRepository educatorInfoRepository;
//    private final SmsService smsService;
//
//    public void signUp(MemberSignUpRequestDTO signUpRequestDTO) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
//        // 회원 정보 생성
//        Member member = new Member();
//        member.setUsername(signUpRequestDTO.getUsername());
//        member.setPassword(signUpRequestDTO.getPassword()); // 비밀번호 해싱 필요
//        member.setEmail(signUpRequestDTO.getEmail());
//        member.setBirthdate(signUpRequestDTO.getBirthdate());
//        member.setMemberType(signUpRequestDTO.getMemberType());
//        member.setPhoneNumber(signUpRequestDTO.getPhoneNumber());
//        member.setEmailConsent(signUpRequestDTO.isEmailConsent());
//        member.setSmsConsent(signUpRequestDTO.isSmsConsent());
//
//        // 회원 정보 저장
//        memberRepository.save(member);
//
//
//}
