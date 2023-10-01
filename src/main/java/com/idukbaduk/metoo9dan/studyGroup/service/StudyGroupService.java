package com.idukbaduk.metoo9dan.studyGroup.service;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.StudyGroups;
import com.idukbaduk.metoo9dan.studyGroup.dto.GroupsDetailListDTO;
import com.idukbaduk.metoo9dan.studyGroup.dto.StudyGroupsListDTO;
import com.idukbaduk.metoo9dan.studyGroup.repository.GameContentRepository;
import com.idukbaduk.metoo9dan.studyGroup.repository.GroupMakeRepository;
import com.idukbaduk.metoo9dan.studyGroup.repository.MemberRepository;
import com.idukbaduk.metoo9dan.studyGroup.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMakeRepository groupMakeRepository;
    private final GameContentRepository gameContentRepository;
    private final MemberRepository memberRepository;

    public List<StudyGroupsListDTO> getList(int member_no) {
        return studyGroupRepository.getGroupList(member_no);
    }

    public List<GroupsDetailListDTO> getDetailList(int group_no) {
        return studyGroupRepository.getGroupDetailList(group_no);
    }
    public List<GroupsDetailListDTO> getGroupInfo(int group_no) {
        return studyGroupRepository.getGroupInfo(group_no);
    }

    public void add(String group_name, Integer group_size, Date group_start_date, Date group_finish_date, String group_introduce,  GameContents gameContents, Member member) {
        StudyGroups studyGroups = new StudyGroups();
        studyGroups.setGroupName(group_name);
        studyGroups.setGroupSize(group_size);
        studyGroups.setGroupStartDate(group_start_date);
        studyGroups.setGroupFinishDate(group_finish_date);
        studyGroups.setGroupIntroduce(group_introduce);
        studyGroups.setGameContents(gameContents);  // 게임 콘텐츠 엔티티 객체 직접 사용
        studyGroups.setMember(member);
        //studyGroups.setGameContents(gameContentRepository.findById(game_content_no).orElse(null));
        //studyGroups.setMember(memberRepository.findById(member_no).orElse(null));
        groupMakeRepository.save(studyGroups);
    }
}
