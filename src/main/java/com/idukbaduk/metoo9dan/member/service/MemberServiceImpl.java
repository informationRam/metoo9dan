package com.idukbaduk.metoo9dan.member.service;

import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.repository.EducatorinfoRepository;
import com.idukbaduk.metoo9dan.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final EducatorinfoRepository educatorInfoRepository;

    @Transactional
    public void createUserWithEducatorInfo(Member member, EducatorInfo educatorInfo) {

        member.setJoinDate(LocalDateTime.now()); // 현재 날짜 및 시간을 설정
        memberRepository.save(member);
        // EducatorInfo 엔터티 저장
        educatorInfo.setMember(member); // 교육자 정보의 memberNo를 설정
        educatorInfoRepository.save(educatorInfo); // EducatorInfo 엔티티를 저장
    }

    @Transactional
    public void createUser(Member member) {

        member.setJoinDate(LocalDateTime.now()); // 현재 날짜 및 시간을 설정
        // Member 엔터티 저장
        memberRepository.save(member);
    }

}



//@RequiredArgsConstructor
//@Service
//public class MemberService {
//    private final MemberRepository memberRepository;
//    private final EducatorinfoRepository educatorInfoRepository;
//    private final SmsService smsService;
//
//    public void createUser(MemberSignUpRequestDTO signUpRequestDTO) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
//
//    }
//    // 회원 정보 생성
//    Member member = new Member();
//        member.setUsername(signUpRequestDTO.getUsername());
//        member.setPassword(signUpRequestDTO.getPassword()); // 비밀번호 해싱 필요
//        member.setEmail(signUpRequestDTO.getEmail());
//        member.setBirthdate(signUpRequestDTO.getBirthdate());
//        member.setMemberType(signUpRequestDTO.getMemberType());
//        member.setPhoneNumber(signUpRequestDTO.getPhoneNumber());
//        member.setEmailConsent(signUpRequestDTO.isEmailConsent());
//        member.setSmsConsent(signUpRequestDTO.isSmsConsent());
//
//    // 회원 정보 저장
//        memberRepository.save(member);
//
//
//}
