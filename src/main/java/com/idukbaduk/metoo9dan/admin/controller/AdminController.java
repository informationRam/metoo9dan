package com.idukbaduk.metoo9dan.admin.controller;

import com.idukbaduk.metoo9dan.admin.dto.DeleteMembersRequest;
import com.idukbaduk.metoo9dan.admin.service.EducatorInfoService;
import com.idukbaduk.metoo9dan.admin.service.MemPaymentsService;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.dto.EducatorInfoDTO;
import com.idukbaduk.metoo9dan.member.service.MemberService;
import com.idukbaduk.metoo9dan.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.idukbaduk.metoo9dan.common.util.DateTimeUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminController {

    private final MemberService memberService;
    private final MemPaymentsService memPaymentsService;
    private final EducatorInfoService educatorInfoService;
    private final ModelMapper modelMapper;

    //회원관리페이지 보여주기
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value="/listMember")
    public String showMemberList(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 Principal principal) throws Exception {
        Pageable pageable = PageRequest.of(page,15); //한 목록에 15개씩
        Page<Member> memberPage = memberService.findAllMembers(pageable); // 모든 회원의 목록

        // joinDate를 원하는 형식의 문자열로 변환
        List<String> formattedJoinDates = memberPage.getContent().stream()
                .map(member -> DateTimeUtils.formatLocalDateTime(member.getJoinDate(),"yyyy.MM.dd")) //.map스트림요소 변환
                .collect(Collectors.toList()); //.collect 스트림요소 수집
        //사이드바 쓰기 위한 memberRole 로직 추가
        String memberRole = null;
        if(principal != null){
            memberRole = memberService.getUser(principal.getName()).getRole();
        }
        if(principal == null || !memberRole.equalsIgnoreCase("admin")){
            model.addAttribute("memberRole", "notAdmin");
        } else {
            model.addAttribute("memberRole", memberRole);
        }

        model.addAttribute("members", memberPage.getContent()); //회원정보
        model.addAttribute("joinDate", formattedJoinDates); //형식변환 joinDate
        model.addAttribute("totalPages", memberPage.getTotalPages());
        model.addAttribute("currentPage",page); //현재페이지: 페이지0부터 시작

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
    //회원 단일 삭제 - 모달
    @Transactional
    @DeleteMapping("/{memberNo}/delete")
    public ResponseEntity<String> deleteMember(@PathVariable("memberNo") Integer memberNo) {
        memberService.deleteMemberByMemberNo(memberNo);
        return new ResponseEntity<>("Member deleted successfully", HttpStatus.OK);
    }

    //회원 다중선택 삭제
    @PostMapping("/deleteMembers")
    public ResponseEntity<String> deleteSelectedMembers(@RequestBody DeleteMembersRequest deleteDTO) {

        try {
            // 클라이언트로부터 받아온 회원 번호들의 리스트
            List<Integer> memberNos = deleteDTO.getMemberNos();
            // 해당 회원들을 삭제하는 서비스 메서드 호출
            memberService.deleteAllMembers(memberNos);
            // 삭제가 성공적으로 수행되면 OK 상태와 메시지 전송
            return new ResponseEntity<>("회원 삭제가 완료되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 삭제 중 오류가 발생했습니다.");
        }
    }
}