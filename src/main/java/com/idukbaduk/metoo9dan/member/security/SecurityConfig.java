package com.idukbaduk.metoo9dan.member.security;


import com.idukbaduk.metoo9dan.member.security.handler.CustomAuthenticationSuccessHandler;
import com.idukbaduk.metoo9dan.member.service.UserSecurityService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration //  스프링 환경설정 파일임을 공지
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)//@PreAuthorize("isAuthenticated()")//로그인인증가 동작할 수 있기 위함
@EnableWebSecurity  // 모든 요청 url이 스프링 시큐리티의 제어를 받도록 만든다.
public class SecurityConfig {

    private final UserSecurityService userDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    //UserSecurityService와 PasswordEncoder가 자동으로 설정
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws  Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/resources/**");
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //유저 권한 등록 test용
    /*UserDetails - spring security에서 사용자의 정보를 담는 인터페이스*/
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails normal = User.builder()
//                .username("normal").password(passwordEncoder().encode("password") ).roles("normal").build();
//        UserDetails student = User.builder()
//                .username("student").password(passwordEncoder().encode("password")).roles("STUDENT").build();
//        UserDetails educator = User.builder()
//                .username("educator").password(passwordEncoder().encode("password")).roles("EDUCATOR").build();
//        UserDetails admin = User.builder()
//                .username("admin").password(passwordEncoder().encode("password")).roles("ADMIN").build();
//        return new InMemoryUserDetailsManager(normal, student, educator, admin);
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/student/**").hasAuthority("STUDENT")
                .requestMatchers( "/admin/**").hasAuthority("ADMIN")
                .requestMatchers( "/member/login").permitAll()
                .anyRequest().permitAll()
                );
        http
                .sessionManagement()
                .sessionFixation()
                .migrateSession() // 세션 변조 공격 방지
                .invalidSessionUrl("/logout") // 세션 만료 시 리디렉션할 URL
                .maximumSessions(1) // 한 사용자당 최대 1개의 세션 허용 (동시 다중 로그인 방지)
                .maxSessionsPreventsLogin(false); // 다른 장치에서 로그인하면 이전 세션 만료
        http.
             formLogin()
                  .loginPage("/member/login")
                  .failureUrl("/")// 사용자 정의 로그인 페이지 =>인증받지 않아도 접근 가능하게 해야함
                  .permitAll()                              //인증받지 않아도 모두 접근가능:없으면 무한루프생김
                  .usernameParameter("memberId")                   // 아이디 파라미터명 설정
                  .passwordParameter("password")                      // 패스워드 파라미터명 설정
                 // .defaultSuccessUrl("/")                   // 로그인 성공 후 이동 페이지 :/loginSuccess
                .successHandler(customAuthenticationSuccessHandler);


        http
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/");

       return http.build();

    }

}
