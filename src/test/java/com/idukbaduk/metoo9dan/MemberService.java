package com.idukbaduk.metoo9dan;

import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EducatorinfoRepository educatorinfoRepository;

    @Transactional
    @Test
    public void registerEducator(Member member, EducatorInfo educatorInfo) {
        // Member 엔터티 저장
        memberRepository.save(member);

        // EducatorInfo 엔터티 저장
        educatorInfo.setMember(member); // 교육자 정보의 memberNo를 설정
        educatorinfoRepository.save(educatorInfo);
    }
}
