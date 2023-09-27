package com.idukbaduk.metoo9dan.studyGroup.service;

import com.idukbaduk.metoo9dan.studyGroup.dto.GroupsDetailListDTO;
import com.idukbaduk.metoo9dan.studyGroup.dto.StudyGroupsListDTO;
import com.idukbaduk.metoo9dan.studyGroup.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;

    public List<StudyGroupsListDTO> getList(int member_no) {
        return studyGroupRepository.getGroupList(member_no);
    }

    public List<GroupsDetailListDTO> getDetailList(int group_no) {
        return studyGroupRepository.getGroupDetailList(group_no);
    }
    public List<GroupsDetailListDTO> getGroupInfo(int group_no) {
        return studyGroupRepository.getGroupInfo(group_no);
    }
}
