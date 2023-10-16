package com.idukbaduk.metoo9dan.member.service;

import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.exception.DataNotFoundException;
import com.idukbaduk.metoo9dan.member.repository.EducatorinfoRepository;
import com.idukbaduk.metoo9dan.member.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final EducatorinfoRepository educatorInfoRepository;

    //회원가입 처리 : roled이 educator일때
    @Transactional
    public void createUserWithEducatorInfo(Member member, EducatorInfo educatorInfo) {

        member.setJoinDate(LocalDateTime.now()); // 현재 날짜 및 시간을 설정
        memberRepository.save(member);
        // EducatorInfo 엔터티 저장
        educatorInfo.setMember(member); // 교육자 정보의 memberNo를 설정
        educatorInfoRepository.save(educatorInfo); // Educator Info 엔티티를 저장
    }

    //회원가입 처리 : roled이 그 외
    @Transactional
    public void createUser(Member member) {

        member.setJoinDate(LocalDateTime.now()); // 현재 날짜 및 시간을 설정
        // Member 엔터티 저장
        memberRepository.save(member);
    }

    //id로 회원정보 가져오기
    public Member getUser(String memberId){
        System.out.println("getUser진입");
        Optional<Member> member = memberRepository.findByMemberId(memberId);
        if (member.isPresent()) {
            return member.get();
        }else{
            throw new DataNotFoundException("해당 ID에 일치하는 회원이 없습니다.");
        }
    }

    //이메일 넣어 회원정보 찾기
    public Member getUserbyEmail (String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            return member.get();
        }else{
            throw new DataNotFoundException("해당 이메일에 일치하는 회원이 없습니다.");
        }
    }
    // 회원 가입시 이메일 중복 여부 확인 or id 찾기에 사용
    @Transactional(readOnly = true)
    public boolean checkEmailDuplication(String email) {
        System.out.println("서비스들어옴");
        boolean emailDuplicate = memberRepository.existsByEmail(email);
        return emailDuplicate;
    }
    //정보 수정시 사용
    @Transactional(readOnly = true)
    public boolean checkEmailDuplication(Member member,MemberModifyForm memberModifyForm) {
        boolean emailDuplicate = false;
        if(!member.getEmail().equals(memberModifyForm.getEmail())){     //기존 이메일값, 변경한 이메일값이 다르면
            emailDuplicate = memberRepository.existsByEmail(memberModifyForm.getEmail());   //회원의 이메일값과 비교를 한다.
        }
        return emailDuplicate;
    }

    // 회원 가입시 아이디 중복 여부 확인
    @Transactional(readOnly = true)
    public boolean checkmemberIdDuplication(String memberId) {
        System.out.println("서비스들어옴");
        boolean emailDuplicate = memberRepository.existsBymemberId(memberId);
        return emailDuplicate;
    }

    //아이디 찾기 - 이메일로 찾기
    public String searchId(String email) {
        System.out.println("searchId서비스 진입");
        Optional<Member> idSearchMem = memberRepository.findByEmail(email);
        if (idSearchMem.isPresent()) {
            Member member = idSearchMem.get();
            return member.getMemberId();
        } else {
            return null; // 해당 이메일을 가진 사용자가 없을 경우
        }
    }

    //비밀번호찾기 - id & email값 동시에 일치하는 회원이 있는지
    public boolean searchPwd(String memberId,String email){
        System.out.println("searchPwd 서비스 진입");
        Optional<Member> pwdSearchMem = memberRepository.findBymemberIdAndEmail(memberId,email);
        System.out.println("회원ID: "+memberId);
        System.out.println("email: "+email);
        System.out.println("pwdSearchMem.isEmpty()?"+pwdSearchMem.isEmpty());
        if(pwdSearchMem.isEmpty()){   //값이 비어있다면
            return true;
        }else {
            return false;
        }
    }

    // 임시 비번 변경
    public void userPwdModify(Member member, String tempPassword){
       /* member.setPassword(passwordEncoder.encode(tempPassword));*/
        memberRepository.save(member);
    }



}