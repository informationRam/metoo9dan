package com.idukbaduk.metoo9dan.member.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration //  스프링 환경설정 파일임을 공지
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)//@PreAuthorize("isAuthenticated()")//로그인인증가 동작할 수 있기 위함
@EnableWebSecurity  // 모든 요청 url이 스프링 시큐리티의 제어를 받도록 만든다.
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

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
    public BCryptPasswordEncoder PasswordEncoder(){
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
        http
            .csrf().disable()
            .authorizeHttpRequests()
                .requestMatchers( new AntPathRequestMatcher("/member/mypage")).hasRole("STUDENT")
                .requestMatchers( new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                .requestMatchers( new AntPathRequestMatcher("/edu/**")).hasRole("EDUCATOR")
                .requestMatchers(new AntPathRequestMatcher("/member/login")).denyAll() //로그인 후 로그인창 접근불가
                .requestMatchers(new AntPathRequestMatcher("/member/join")).denyAll() //로그인 후 회원가입 접근불가
                //  auth.requestMatchers("/user/**").hasAnyRole("ADMIN", "USER");
                .anyRequest().permitAll() //그외는 인증을 받지 않음

        .and()
             .formLogin()
                  .loginPage("/member/login")             // 사용자 정의 로그인 페이지 =>인증받지 않아도 접근 가능하게 해야함
                  .usernameParameter("memberId")         //로그인처리시 아이디 파리미터명 설정
                  .passwordParameter("password")         //패스워드 파라미터명 설정
                  .defaultSuccessUrl("/")              // 로그인 성공 후 이동 페이지
                  .permitAll(); // 로그인 페이지는 인증 필요 없음
//                .failureUrl("/member/login")           // 로그인 실패 후 이동 페이지
//
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

        http
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll();
//                .addLogoutHandler(new LogoutHandler() {
//                    @Override
//                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//                        HttpSession session = request.getSession();
//                        session.invalidate();
//                    }
//                })


        return http.build();

    }

}
