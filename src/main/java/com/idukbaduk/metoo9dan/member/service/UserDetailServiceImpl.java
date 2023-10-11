package com.idukbaduk.metoo9dan.member.service;

import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//시큐리티 로그인을 도와주는 서비스: 인증 Authentiation 프로세스 구현

@Service("userDetailsService")
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        System.out.println("시큐리티 로그인 진행 : CustomUserDetailService 진입");

        Member member = memberRepository.findByMemberId(memberId);
        System.out.println("member: " + member);
        System.out.println("memberId: " + memberId);

        if(member == null){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다." + memberId);
        }

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(member.getRole()));


        //권한정보 전달
        MemberContext memberContext = new MemberContext(member,roles);

        //사용자 객체 리턴(userDetails타입)
        return memberContext;

    }

}
