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

}
