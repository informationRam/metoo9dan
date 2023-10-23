package com.idukbaduk.metoo9dan.member.controller;


import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.GroupStudents;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.dto.EducatorInfoDTO;
import com.idukbaduk.metoo9dan.member.dto.MemberDTO;
import com.idukbaduk.metoo9dan.member.service.MemberService;
import com.idukbaduk.metoo9dan.member.validation.UserCreateForm;
import com.idukbaduk.metoo9dan.studyGroup.repository.GroupStudentsRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/member")
@RequiredArgsConstructor
@Controller

public class memberController {

    private final MemberService memberService;
    private final GroupStudentsRepository groupStudentsRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //test- 마이페이지 접근
    @GetMapping(value = "/mypage/{memberId}")
    public String myPage(@PathVariable("memberId") String memberId,
                         Model model, Principal principal) throws Exception {
        Member member = memberService.getUser(principal.getName());
        String memname = member.getName(); //  회원이름
        String Id = member.getMemberId(); // 회원아이디
        String Role = member.getRole(); // 역할


        System.out.println("이름" + memname);
        System.out.println("아이디" + Id);

        model.addAttribute("member", member); // 로그인 사용자 전체 정보
        model.addAttribute("role", Role); // 로그인 사용자 전체 정보
        model.addAttribute("memberName", memname); // 로그인 회원 이름
        return "/member/myPage";
    }

    //id찾기
    @PostMapping("/idSearch")
    public ResponseEntity<String> searchId(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String memberId = memberService.findMemberIdByEmail(email);
        if (memberId != null) {
            return ResponseEntity.ok(memberId);
        } else {
            return ResponseEntity.ok("해당 이메일로 가입한 사용자가 없습니다.");
        }
    }




    //로그인 & 회원가입 폼 화면 보여주기
    @GetMapping("/login")
    public String login(Model model, UserCreateForm userCreateForm) {
        model.addAttribute("userCreateForm", userCreateForm);
        return "member/signupForm2";
    }

    //회원가입 처리하기
    //사용자 정보를 memberDTO에 받고,이를 member Entity에 옮기고 DB에 저장한다.
    @PostMapping("/join")
    public String createUser(@Valid UserCreateForm form, BindingResult bindingResult, Model model) {
        ModelMapper modelMapper = new ModelMapper();
        System.out.println("회원가입 컨트롤러 진입");

        //패스워드 암호화
        MemberDTO memberDTO = modelMapper.map(form, MemberDTO.class);
        memberDTO.setPassword(passwordEncoder.encode(form.getPwd1()));

        if ("EDUCATOR".equals(memberDTO.getRole())) {
            // EducatorInfoDTO에 관련 정보를 매핑합니다.
            EducatorInfoDTO educatorInfoDTO = new EducatorInfoDTO();
            educatorInfoDTO.setSido(form.getSido());
            educatorInfoDTO.setSigungu(form.getSigungu());
            educatorInfoDTO.setSchoolName(form.getSchoolName());
            // EducatorInfoDTO 데이터를 EducatorInfo 엔티티로 이동
            EducatorInfo educatorInfo = modelMapper.map(educatorInfoDTO, EducatorInfo.class);
            // MemberDTO 데이터를 Member 엔티티로 이동
            Member member = modelMapper.map(memberDTO, Member.class);

            memberService.createUserWithEducatorInfo(member, educatorInfo);

        } else {
            // MemberDTO 데이터를 Member 엔티티로 이동
            Member member = modelMapper.map(memberDTO, Member.class);
            memberService.createUser(member);
        }
        return "redirect:/member/login"; //회원가입 후 로그인 페이지
    }

    //id&pw 찾기 화면
    @GetMapping("/idpwSearch")
    public String searchidform() {

        return "member/forgotAccount";

    }

    @PostMapping("/checkPhoneNumberDuplication")
    @ResponseBody //json응답반환
    public Map<String, Boolean> checkPhoneNumberDuplication(@RequestBody Map<String, String> request) {
        String tel = request.get("tel");
        System.out.println("휴대폰번호 중복 확인 요청: " + tel);

        boolean isDuplicate = memberService.checkmemberTelDuplication(tel);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);
        return response;

    }

    //이메일 중복검사
    @PostMapping("/checkEmailDuplication")
    @ResponseBody //json응답반환
    public ResponseEntity<Map<String, Boolean>> checkEmailDuplication(@RequestBody Map<String, String> request) {
        String email = request.get("valiEmail");
        boolean isDuplicate = memberService.checkEmailDuplication(email);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);

        return ResponseEntity.ok(response);
    }

    //아이디 중복검사
    @ResponseBody
    @PostMapping("/checkMemberIdDuplication")
    public Map<String, Boolean> checkMemberIdDuplication(@RequestBody Map<String, String> request) {
        String memberId = request.get("memberId");
        boolean isDuplicated = memberService.checkmemberIdDuplication(memberId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicated", isDuplicated);
        return response;
    }

    @GetMapping("/loginSuccess")
    public String handleLoginSuccess(Principal principal) {
        Member member = memberService.getUser(principal.getName());

        if ("STUDENT".equals(member.getRole())) {
            // 학습 그룹 가입 여부 확인
            Optional<GroupStudents> groupStudents = groupStudentsRepository.findByMemberAndIsApprovedIsFalse(member);
            if (groupStudents.isPresent()) {
                // 가입되지 않은 학습 그룹이 있다면 가입신청 페이지로 리디렉션
                return "redirect:/applygroup";
            }
        } else if ("NORMAL".equals(member.getRole()) || "EDUCATOR".equals(member.getRole())) {
            if ("무료회원".equals(member.getMembershipStatus())) {
                // 무료 회원인 경우 게임 구매 페이지로 리디렉션
                return "redirect:/game/purchase";
            }
        }

        // 그 외의 경우에는 메인페이지
        return "redirect:/";
    }
}
