package com.idukbaduk.metoo9dan.studyGroup.service;

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

    public List<StudyGroupsListDTO> getList(int page) {
        List<Sort.Order> sorts = new ArrayList();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,10, Sort.by(sorts));
        return studyGroupRepository.getList();
    }
}
