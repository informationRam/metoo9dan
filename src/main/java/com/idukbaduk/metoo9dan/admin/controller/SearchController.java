package com.idukbaduk.metoo9dan.admin.controller;

import com.idukbaduk.metoo9dan.admin.service.AdminService;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.dto.MemberDTO;
import com.idukbaduk.metoo9dan.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class SearchController {
    private final AdminService adminService;

    @GetMapping("/search")
    public ResponseEntity<Page<MemberDTO>> search(
                                    @RequestParam String startDate,
                                    @RequestParam String endDate,
                                    @RequestParam String memberType,
                                    @RequestParam String membershipStatus,
                                    @RequestParam String searchCriteria,
                                    @RequestParam String searchKeyword,
                                    @RequestParam int page,
                                    @RequestParam int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        //검색된 회원 정보, 페이지네이션 정보(전체 페이지 수, 현재 페이지 번호, 페이지당 항목 수 등)도 포함
        Page<MemberDTO> result = adminService.searchMembers(startDate, endDate, memberType, membershipStatus, searchCriteria, searchKeyword, pageable);

        return ResponseEntity.ok(result); // 200 OK와 함께 검색 결과 반환
    }
}
