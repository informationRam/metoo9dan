package com.idukbaduk.metoo9dan.admin.service;

import com.idukbaduk.metoo9dan.admin.domain.MemberSpecification;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.dto.MemberDTO;
import com.idukbaduk.metoo9dan.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    //다중 검색 처리
    //entity로 받은 검색결과 DTO 변환
    public Page<MemberDTO> searchMembers(String startDate, String endDate, String memberType, String membershipStatus, String searchCriteria, String searchKeyword, Pageable pageable) {
        // 검색 조건에 따라 엔티티를 조회
        Specification<Member> spec = MemberSpecification.searchMembers(startDate, endDate, memberType, membershipStatus, searchCriteria, searchKeyword);
        Page<Member> members = memberRepository.findAll(spec, pageable);
        // 검색 결과 엔티티를 DTO로 변환
        Page<MemberDTO> memberDTOs =  members.map(member -> modelMapper.map(member, MemberDTO.class));
        return memberDTOs;
    }



}