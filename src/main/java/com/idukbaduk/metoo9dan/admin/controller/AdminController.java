package com.idukbaduk.metoo9dan.admin.controller;

import com.idukbaduk.metoo9dan.admin.service.EducatorInfoService;
import com.idukbaduk.metoo9dan.admin.service.memPaymentsService;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.dto.EducatorInfoDTO;
import com.idukbaduk.metoo9dan.member.service.MemberService;
import com.idukbaduk.metoo9dan.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.idukbaduk.metoo9dan.common.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminController {

    private final MemberService memberService;
    private final memPaymentsService memPaymentsService;
    private final EducatorInfoService educatorInfoService;
    private final ModelMapper modelMapper;

    //회원관리페이지 보여주기
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value="/listMember")
    public String showMemberList(Model model) throws Exception {
        List<Member> members = memberService.getAllMembers(); // 모든 회원의 목록

        // joinDate를 원하는 형식의 문자열로 변환
        List<String> formattedJoinDates = new ArrayList<>();
        for (Member member : members) {
            String formattedDate = DateTimeUtils.formatLocalDateTime(member.getJoinDate(), "yyyy.MM.dd");
            formattedJoinDates.add(formattedDate);
        }

        model.addAttribute("members", members);
        model.addAttribute("joinDate", formattedJoinDates);
        return "admin/memberList";
    }

    //회원 상세정보 데이터로 보내기
    @GetMapping("/members/{memberNo}")
    public ResponseEntity<MemberDTO> getMemberDetails(@PathVariable("memberNo") Integer memberNo) {
        MemberDTO memberDTO = memberService.getMemberByMemberNo(memberNo);
        if (memberDTO != null) {
            return new ResponseEntity<>(memberDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //교육자 정보 데이터로 보내기
    @GetMapping("/members/{memberNo}/educatorInfo")
    public ResponseEntity<EducatorInfoDTO> getEducatorInfoByMemberNo(@PathVariable("memberNo") Integer memberNo) {
        MemberDTO memberDTO = memberService.getMemberByMemberNo(memberNo);

        // member가 null인 경우나 member의 role이 EDUCATOR가 아닌 경우 교육자 정보를 가져오지 않습니다.
        if (memberDTO != null && "EDUCATOR".equals(memberDTO.getRole())) {
            EducatorInfoDTO educatorInfoDTO = educatorInfoService.getEducatorInfoByMemberNo(memberNo);

            if (educatorInfoDTO != null) {
                return new ResponseEntity<>(educatorInfoDTO, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    //회원 자격(유료/무료회원) 가져오기
    @GetMapping("/members/{memberNo}/membershipStatus")
    public ResponseEntity<String> getMembershipStatus(@PathVariable("memberNo") Integer memberNo) {
        String membershipStatus = memberService.getMembershipStatusByMemberNo(memberNo);
        if (membershipStatus != null) {
            return new ResponseEntity<>(membershipStatus, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    //회원 구매건수 가져오기
    @GetMapping("/members/{memberNo}/payments")
    public ResponseEntity<Map<String, Integer>> getMemberPaymentsCount(  @PathVariable("memberNo") Integer memberNo) {
        int paymentCount = memPaymentsService.getPaymentCountByMemberNo(memberNo);
        Map<String, Integer> response = new HashMap<>();
        response.put("count", paymentCount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //회원정보 업데이트 - MEMBER 테이블
    @PutMapping ("/members/{memberNo}/updateMemberData")
    public ResponseEntity<String> updateMember( @PathVariable("memberNo") Integer memberNo,
                                                @RequestBody MemberDTO updatedMemberData ){
        boolean memberUpdateSuccess= memberService.updateMemberData(memberNo, updatedMemberData);
        System.out.println("member 업데이트 진입");
        if (memberUpdateSuccess) {
            return new ResponseEntity<>("Member 데이터 수정 성공 ", HttpStatus.OK);


        } else {
            return new ResponseEntity<>("Member 데이터 수정 실패 ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //회원정보 업데이트 - EDUCATOR테이블
    @PutMapping ("/members/{memberNo}/updateEducatorData")
    public ResponseEntity<String> updateEducator (@PathVariable("memberNo") Integer memberNo,
                                                  @RequestBody EducatorInfoDTO updatedEducatorData ){
        boolean educatorUpdateSuccess = educatorInfoService.updateEducatorData(memberNo, updatedEducatorData);

        if (educatorUpdateSuccess) {
            return new ResponseEntity<>("EducatorInfo 데이터 수정 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("EducatorInfo 데이터 수정 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}