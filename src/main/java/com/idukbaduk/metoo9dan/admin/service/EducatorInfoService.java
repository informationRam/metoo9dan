package com.idukbaduk.metoo9dan.admin.service;

import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.member.dto.EducatorInfoDTO;
import com.idukbaduk.metoo9dan.member.repository.EducatorinfoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EducatorInfoService {

    private final EducatorinfoRepository educatorInfoRepository;

    //교육자 정보 조회
    public EducatorInfoDTO getEducatorInfoByMemberNo(Integer memberNo) {
        EducatorInfo educatorInfo = educatorInfoRepository.findByMemberMemberNo(memberNo);
        if (educatorInfo != null) {
            // EducatorInfo 엔티티를 EducatorInfoDTO로 매핑
            ModelMapper modelMapper = new ModelMapper();
            EducatorInfoDTO educatorInfoDTO = modelMapper.map(educatorInfo, EducatorInfoDTO.class);
            return educatorInfoDTO;
        }
        return null; // 교육자 정보가 없는 경우 null을 반환하거나 다른 방식으로 처리
    }

    //교육자 정보 수정
    public boolean updateEducatorData(Integer memberNo, EducatorInfoDTO updatedEducatorData) {
        // Retrieve the EducatorInfo entity by memberNo
        EducatorInfo educatorInfo = educatorInfoRepository.findByMemberMemberNo(memberNo);

        if (educatorInfo != null) {
            // Update the EducatorInfo entity with the new data
            educatorInfo.setSchoolName(updatedEducatorData.getSchoolName());
            educatorInfo.setSido(updatedEducatorData.getSido());
            educatorInfo.setSigungu(updatedEducatorData.getSigungu());

            // Save the updated EducatorInfo entity back to the database
            educatorInfoRepository.save(educatorInfo);

            return true;
        }

        return false;
    }
}