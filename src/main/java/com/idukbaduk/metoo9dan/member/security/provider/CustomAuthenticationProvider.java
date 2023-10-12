/*
package com.idukbaduk.metoo9dan.member.security.provider;
//사용안함
import com.idukbaduk.metoo9dan.member.service.MemberContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

// 화면에서 입력한 로그인 정보와 DB에서 가져온 사용자의 정보를 비교해주는 인터페이스

public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    */
/*authentication : authenticationManager 클래스로 부터 전달받는 인증객체
      사용자가 입력한 ID, Pwd정보가 담겨져 있음*//*

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
       //인증에 대한 추가 검증
        String memberId = auth.getName();
        String password = (String)auth.getCredentials(); //password변수 사용자가 입력한 비밀번호

        MemberContext memberContext = (MemberContext)userDetailsService.loadUserByUsername(memberId);  //데이터에 저장된 정보 , id는 여기서 검증

        if(Objects.isNull(memberContext)) {
            throw new BadCredentialsException("존재하지 않는 회원 입니다");
        }
        //비밀번호 검증
        else if(!this.passwordEncoder.matches(password,memberContext.getPassword())){    //password 사용자 입력, memberContext.getPassword() 데이터 저장된 pwd
          throw new BadCredentialsException("비밀번호가 일치하지 않습니다");
        }

        //인증에 모두 성공시 token을 만듦
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                memberContext.getMember(),null,memberContext.getAuthorities());

        return authenticationToken;
    }

    */
/*username,password,autnentication 토큰이
    현재 파라미터로 전달된 이 클래스 타입과 일치하면 인증 *//*

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
*/
