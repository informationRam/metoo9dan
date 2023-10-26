package com.idukbaduk.metoo9dan.member.security.handler;

import com.idukbaduk.metoo9dan.common.entity.GroupStudents;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.repository.MemberRepository;
import com.idukbaduk.metoo9dan.studyGroup.repository.GroupStudentsRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    private final MemberRepository memberRepository;

    private final GroupStudentsRepository groupStudentsRepository;
    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        //기본 페이지 설정
        setDefaultTargetUrl("/");

        // 사용자의 정보를 불러옵니다.
        Member member = memberRepository.findByMemberId(authentication.getName()).orElse(null);  // User -> Member로 변경

        if (member == null) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // 사용자의 권한 및 멤버십 상태를 기반으로 리디렉션을 정합니다.
                String redirectURL = determineTargetUrl(member);
                getRedirectStrategy().sendRedirect(request, response, redirectURL);
            }

    private String determineTargetUrl(Member member) {  // User -> Member로 변경
        // STUDENT 역할을 확인하고 학습 그룹 가입 여부 확인
        if ("STUDENT".equals(member.getRole())) {
            Optional<GroupStudents> groupStudents = groupStudentsRepository.findByMemberAndIsApprovedIsFalse(member);
            if (groupStudents.isPresent()) {
                return "/studygroup/groupJoinList";
            }
        }

        // 일반인이나 교육자의 경우
        if (("NORMAL".equals(member.getRole()) || "EDUCATOR".equals(member.getRole())) && "무료회원".equals(member.getMembershipStatus())) {
            return "/game/alllist";
        }

        return "/";
    }
}


    //이전에 저장된 정보인 requestCache 가져와서 원래 가고자 햇떤 곳으로

//       SavedRequest savedRequest = requestCache.getRequest(request,response); //사용자가 요청전에 담고있는 정보 (로그인전,시도시)
//        if(savedRequest!=null) { //로그인시도시 다른데 접근했다가 인증튕겨서 올경우 null
//            String targetUrl = savedRequest.getRedirectUrl();
//            redirectStrategy.sendRedirect(request, response, targetUrl);
//        }else{
//            redirectStrategy.sendRedirect(request,response,getDefaultTargetUrl());
//
//        }
//    }

//}
