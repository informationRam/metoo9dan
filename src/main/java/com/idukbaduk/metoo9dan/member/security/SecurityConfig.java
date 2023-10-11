package com.idukbaduk.metoo9dan.member.security;

import com.idukbaduk.metoo9dan.member.security.provider.CustomAuthenticationProvider;
import com.idukbaduk.metoo9dan.member.service.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration //  스프링 환경설정 파일임을 공지
@RequiredArgsConstructor
@EnableWebSecurity  // 모든 요청 url이 스프링 시큐리티의 제어를 받도록 만든다.
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    //UserSecurityService와 PasswordEncoder가 자동으로 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider();
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
                //.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers( new AntPathRequestMatcher("/member/mypage")).hasRole("STUDENT")
                .requestMatchers( new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                .requestMatchers( new AntPathRequestMatcher("/edu/**")).hasRole("EDUCATOR")
                .requestMatchers(new AntPathRequestMatcher("/member/join#pills-register")).denyAll() //로그인 후 회원가입접근불가
                //  auth.requestMatchers("/user/**").hasAnyRole("ADMIN", "USER");
                .anyRequest().permitAll()

        .and()
             .formLogin()
                  .loginPage("/member/login")               // 사용자 정의 로그인 페이지 =>인증받지 않아도 접근 가능하게 해야함
                 // .loginProcessingUrl("/member/login")      // 로그인 Form Action Url
                  .defaultSuccessUrl("/")                   // 로그인 성공 후 이동 페이지
                  .permitAll();
//                .failureUrl("/member/login")              // 로그인 실패 후 이동 페이지
//                .usernameParameter("user_id")                   // 아이디 파라미터명 설정
//                .passwordParameter("pwd")                       // 패스워드 파라미터명 설정
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
                .logoutUrl("/logout");
//                .logoutSuccessUrl("/")
//                .addLogoutHandler(new LogoutHandler() {
//                    @Override
//                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//                        HttpSession session = request.getSession();
//                        session.invalidate();
//                    }
//                })
//        // RememberMe
//        .and()
//                .rememberMe()
//                .rememberMeParameter("remember")
//                .tokenValiditySeconds(3600) //1시간
//                .userDetailsService(userDetailsService); //Autowired

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

        return http.build();

    }

}
