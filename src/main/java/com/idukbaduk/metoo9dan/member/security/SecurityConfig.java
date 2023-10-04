package com.idukbaduk.metoo9dan.member.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration //  스프링 환경설정 파일임을 공지
@EnableWebSecurity  // 모든 요청 url이 스프링 시큐리티의 제어를 받도록 만든다.
public class SecurityConfig {

    @Autowired
    UserDetailsService userDetailsService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests()
                .anyRequest().authenticated()
        .and()
                .formLogin();
//                .loginPage("/member/loginPage");       // 사용자 정의 로그인 페이지 =>인증받지 않아도 접근 가능하게 해야함
//                .defaultSuccessUrl("/")                         // 로그인 성공 후 이동 페이지
//                .failureUrl("/member/login")        // 로그인 실패 후 이동 페이지
//                .usernameParameter("user_id")                   // 아이디 파라미터명 설정
//                .passwordParameter("pwd")                       // 패스워드 파라미터명 설정
//                .loginProcessingUrl("/member/login_proc")              // 로그인 Form Action Url
//                .successHandler(new AuthenticationSuccessHandler() {    // 로그인 성공 후 핸들러
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        System.out.println("authentication" + authentication.getName());  //인증성공한 사용자 이름 출력
//                        response.sendRedirect("/");     //성공 후 root 페이지로 이동
//                    }
//                })
//                .failureHandler(new AuthenticationFailureHandler() {
//                    @Override
//                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//                        System.out.println("exception" + exception.getMessage());  //인증 실패 메세지 출력
//                        response.sendRedirect("/member/login");  // 로그인 실패 후 이동 페이지
//                    }
//                })
//                .permitAll(); //인증받지 않아도 모두 접근가능


        http
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .addLogoutHandler(new LogoutHandler() {
                    @Override
                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                        HttpSession session = request.getSession();
                        session.invalidate();
                    }
                })
        // RememberMe
        .and()
                .rememberMe()
                .rememberMeParameter("remember")
                .tokenValiditySeconds(3600) //1시간
                .userDetailsService(userDetailsService); //Autowired

//        //d예외처리
//        http.exceptionHandling()
//                .authenticationEntryPoint(new AuthenticationEntryPoint() {
//                    @Override
//                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//
//                    }
//                })
//                .accessDeniedHandler(new AccessDeniedHandler() {
//                    @Override
//                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//                        response.sendRedirect("/denied");
//                    }
//                });

    // 인증실패 시 처리

        
        return http.build();

    }

}
