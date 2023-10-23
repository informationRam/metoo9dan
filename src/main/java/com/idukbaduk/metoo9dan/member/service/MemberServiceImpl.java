package com.idukbaduk.metoo9dan.member.service;

import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.dto.MemberDTO;
import com.idukbaduk.metoo9dan.member.exception.DataNotFoundException;
import com.idukbaduk.metoo9dan.member.repository.EducatorinfoRepository;
import com.idukbaduk.metoo9dan.member.repository.MemberRepository;
import com.idukbaduk.metoo9dan.payments.repository.PaymentsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PaymentsRepository paymentsRepository;
    private final EducatorinfoRepository educatorInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


    //페이지네이션용
    @Override
    public Page<Member> findAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }
    //모달-단일 회원삭제
    @Transactional
    @Override
    public void deleteMemberByMemberNo(Integer memberNo) {
        memberRepository.deleteByMemberNo(memberNo);
    }
    //게시판 회원 다중 삭제
    @Transactional
    @Override
    public void deleteAllMembers(List<Integer> memberNos) {
        memberRepository.deleteByMemberNoIn(memberNos);
    }

    //Member테이블 수정
    @Override
    public boolean updateMemberData(Integer memberNo, MemberDTO updatedMemberData) {
        // Retrieve the Member entity by memberNo
        Optional<Member> optionalMember = memberRepository.findById(memberNo);
        System.out.println("멤버테이블 수정 진입");
        if (optionalMember.isPresent()) {
           Member member = optionalMember.get();
            // Update the Member entity with the new data
            member.setBirth(updatedMemberData.getBirth());
            member.setTel(updatedMemberData.getTel());
            member.setEmail(updatedMemberData.getEmail());
            member.setMemberMemo(updatedMemberData.getMemberMemo());

            // Save the updated Member entity back to the database
            memberRepository.save(member);
            System.out.println("멤버테이블 수정 완료");
            return true;
        }
        return false;
    }

    @Override
    //회원 자격 조회(무료/유료)
    public String getMembershipStatusByMemberNo(Integer memberNo) {
        boolean isPaidMember = paymentsRepository.existsByMemberMemberNo(memberNo);
        return isPaidMember ? "유료회원" : "무료회원";
    }

    //회원번호로 회원정보 조회
    @Override
    public MemberDTO getMemberByMemberNo(Integer memberNo) {
        Member member = memberRepository.findById(memberNo).orElse(null);
        //entity에 매핑
        if (member != null) {
            return modelMapper.map(member, MemberDTO.class);
        }
        return null;
    }

    //회원정보전체 조회 :자격 포함
    @Override
    public List<Member> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            boolean isPaidMember = paymentsRepository.existsByMemberMemberNo(member.getMemberNo());
          //결제내역 있으면 유료, 없으면 무료회원
            member.setMembershipStatus(isPaidMember ? "유료회원" : "무료회원");
        }
        return members;
    }

    //교육자 회원가입 처리 : role이 educator일때
    @Transactional
    public void createUserWithEducatorInfo(Member member, EducatorInfo educatorInfo) {

        member.setJoinDate(LocalDateTime.now()); // 현재 날짜 및 시간을 설정
        member.setMembershipStatus("무료회원");
        memberRepository.save(member);
        // EducatorInfo 엔터티 저장
        educatorInfo.setMember(member); // 교육자 정보의 memberNo를 설정
        educatorInfoRepository.save(educatorInfo); // Educator Info 엔티티를 저장
    }
    
    //회원가입 처리 : roled이 그 외
    @Override
    @Transactional
    public void createUser(Member member) {

        member.setJoinDate(LocalDateTime.now()); // 현재 날짜 및 시간을 설정
        member.setMembershipStatus("무료회원");

        // Member 엔터티 저장
        memberRepository.save(member);
    }

    //id로 회원정보 가져오기
    @Override
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
    @Override
    public Member getUserbyEmail (String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            return member.get();
        }else{
            throw new DataNotFoundException("해당 이메일에 일치하는 회원이 없습니다.");
        }
    }
    // 회원 가입시 이메일 중복 여부 확인
    @Override
    @Transactional(readOnly = true)
    public boolean checkEmailDuplication(String email) {
        System.out.println("서비스들어옴");
        boolean emailDuplicate = memberRepository.existsByEmail(email);
        return emailDuplicate;
    }
    //정보 수정시 사용
    @Override
    @Transactional(readOnly = true)
    public boolean checkEmailDuplication(Member member,MemberModifyForm memberModifyForm) {
        boolean emailDuplicate = false;
        if(!member.getEmail().equals(memberModifyForm.getEmail())){     //기존 이메일값, 변경한 이메일값이 다르면
            emailDuplicate = memberRepository.existsByEmail(memberModifyForm.getEmail());   //회원의 이메일값과 비교를 한다.
        }
        return emailDuplicate;
    }

    // 회원 가입시 아이디 중복 여부 확인
    @Override
    @Transactional(readOnly = true)
    public boolean checkmemberIdDuplication(String memberId) {
        System.out.println("서비스들어옴");
        boolean emailDuplicate = memberRepository.existsByMemberId(memberId);
        return emailDuplicate;
    }

    // 회원가입시 휴대폰번호 중복 확인
    @Override
    public boolean checkmemberTelDuplication(String tel){
        System.out.println("휴대폰번호 중복 검사 서비스");
        boolean telDuplicate = memberRepository.existsByTel(tel);
        return telDuplicate;
    }



    //아이디 찾기 - 이메일로 찾기
    @Override
    public String findMemberIdByEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        return member.map(Member::getMemberId).orElse(null);
    }


    //비밀번호찾기 - id & email값 동시에 일치하는 회원이 있는지
    @Override
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
    @Override
    public void userPwdModify(Member member, String tempPassword){
        member.setPassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(member);
    }

    
    
}



