package com.idukbaduk.metoo9dan.member.service;

import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//시큐리티 로그인 처리 서비스: 인증 Authentiation 프로세스 구현

@RequiredArgsConstructor
@Service("userDetailsService")
public class UserSecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;

    //-- Param 값으로 Password 를 조회하여 반환하는 method --//
    // 반환값을 이용해 Spring Security 가 자동으로 일치여부를 검사해준다.
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        System.out.println("시큐리티 로그인 진행 : CustomUserDetailService 진입");

        Optional<Member> member = memberRepository.findByMemberId(memberId);

        System.out.println("member: " + member);

        if(member.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다." + memberId);
        }

        //제시한 username을 가진 사용자가 존재하는 경우 아래 코드 진행
        Member member1 = member.get();
        // 클라이언트의 권한을 저장하는 List
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 사용자의 권한에 따라 권한을 추가
        switch (member1.getRole()) {
            case "ADMIN":
                authorities.add(new SimpleGrantedAuthority("ADMIN"));
                break;
            case "STUDENT":
                authorities.add(new SimpleGrantedAuthority("STUDENT"));
                break;
            case "NORMAL":
                authorities.add(new SimpleGrantedAuthority("NORMAL"));
                break;
            case "EDUCATOR":
                authorities.add(new SimpleGrantedAuthority("EDUCATOR"));
                break;
        }
        //사용자 권한 확인
        System.out.println("authorities:" +authorities);
        //사용자 객체 리턴 
        return new User(member1.getMemberId(), member1.getPassword(), authorities);

    }

}
