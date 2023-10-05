package com.idukbaduk.metoo9dan.studyGroup.service;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.GroupStudents;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.StudyGroups;
import com.idukbaduk.metoo9dan.studyGroup.dto.*;
import com.idukbaduk.metoo9dan.studyGroup.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;
    private final GroupRepository groupRepository;
    private final GroupStudentsRepository groupStudentsRepository;
    private final GameContentRepository gameContentRepository;
    private final MemberRepository memberRepository;

    //학습그룹 목록 조회
    public List<StudyGroupsListDTO> getList(int member_no) {
        return studyGroupRepository.getGroupList(member_no);
    }

    //학습그룹 리스트 조회 버튼
    public List<StudyGroupsListDTO> selectGroup(Map<String, Integer> map){
        return studyGroupRepository.selectGroup(map);
    }

    //학습그룹 상세 조회
    public List<GroupsDetailListDTO> getDetailList(int group_no) {
        return studyGroupRepository.getGroupDetailList(group_no);
    }

    //학습그룹인원 정보 가져오기
    public List<GroupsDetailListDTO> getGroupInfo(int group_no) {
        return studyGroupRepository.getGroupInfo(group_no);
    }

    //학습그룹 등록
    public void add(String group_name, Integer group_size, Date group_start_date, Date group_finish_date, String group_introduce,  GameContents gameContents, Member member) {
        StudyGroups studyGroups = new StudyGroups();
        studyGroups.setGroupName(group_name);
        studyGroups.setGroupSize(group_size);
        studyGroups.setGroupStartDate(group_start_date);
        studyGroups.setGroupFinishDate(group_finish_date);
        studyGroups.setGroupIntroduce(group_introduce);
        studyGroups.setGameContents(gameContents);
        studyGroups.setMember(member);
        //studyGroups.setGameContents(gameContentRepository.findById(game_content_no).orElse(null));
        //studyGroups.setMember(memberRepository.findById(member_no).orElse(null));
        groupRepository.save(studyGroups);
    }

    //게임콘텐츠 리스트
    public List<GameContentsListDTO> getGameList(int member_no) {
        return studyGroupRepository.getGameList(member_no);
    }

    //학습그룹 삭제
    public void delete(StudyGroups studyGroups) {
        groupRepository.delete(studyGroups);
    }

    //학습그룹 가져오기
    public StudyGroups getGruop(int group_no) {
        Optional<StudyGroups> studyGroups = groupRepository.findById(group_no);
        return studyGroups.get();
    }

    //학습그룹 수정하기
    public void modify(StudyGroups studyGroups,String group_name, Integer group_size, Date group_start_date, Date group_finish_date, String group_introduce, Member member) {
        studyGroups.setGroupName(group_name);
        studyGroups.setGroupSize(group_size);
        studyGroups.setGroupStartDate(group_start_date);
        studyGroups.setGroupFinishDate(group_finish_date);
        studyGroups.setGroupIntroduce(group_introduce);
        studyGroups.setMember(member);
        groupRepository.save(studyGroups);
    }

    //학습그룹 가입신청 리스트
    public List<GroupJoinListDTO> getGroupJoinList() {
        return studyGroupRepository.getGroupJoinList();
    }

    //학습그룹 신청
    public void groupJoin(StudyGroups studyGroups, Member member, LocalDateTime application_date, Boolean is_approved,LocalDateTime approved_date){
        GroupStudents groupStudents = new GroupStudents();
        groupStudents.setStudyGroups(studyGroups);
        groupStudents.setMember(member);
        groupStudents.setApplicationDate(LocalDateTime.now());
        groupStudents.setIsApproved(Boolean.FALSE);
        groupStudents.setApprovedDate(approved_date);
        groupStudentsRepository.save(groupStudents);
    }

    //학습그룹 신청 리스트
    public List<ApproveListDTO> getApproveList(int member_no) {
        return studyGroupRepository.getApproveList(member_no);
    }

    //학습그룹 승인 신청
    public int updateApproval(List<Integer> groupStudentsNoList){
        return studyGroupRepository.updateApproval(groupStudentsNoList);
    }

    //학습그룹명 가져오기
    public List<HashMap<String, Object>> getGroupName(int member_no){
        return studyGroupRepository.getGroupName(member_no);
    }


}
