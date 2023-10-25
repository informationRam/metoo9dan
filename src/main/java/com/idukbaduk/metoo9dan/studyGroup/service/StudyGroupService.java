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
    private final MemberRepository_studyGroup memberRepositoryStudyGroup;

    //학습그룹 목록 조회
    public List<StudyGroupsListDTO> getList(int member_no) {
        return studyGroupRepository.getGroupList(member_no);
    }

    //학습그룹 목록 조회(페이지네이션)
    public List<StudyGroupsListDTO> getGroupListPage(Map<String, Integer> map) {
        return studyGroupRepository.getGroupListPage(map);
    }

    //학습그룹 리스트 Cnt
    public int getGroupListCnt(int member_no) {
        return studyGroupRepository.getGroupListCnt(member_no);
    }

    //학습그룹 리스트 조회 버튼
    public List<StudyGroupsListDTO> selectGroup(Map<String, Integer> map){
        return studyGroupRepository.selectGroup(map);
    }

    //학습그룹 상세조회(그룹 멤버)
    public List<GroupsDetailListDTO> getDetailList(int group_no) {
        return studyGroupRepository.getGroupDetailList(group_no);
    }

    //학습그룹 상세조회(그룹 정보)
    public List<GroupsDetailListDTO> getGroupInfo(int group_no) {
        return studyGroupRepository.getGroupInfo(group_no);
    }

    //학습그룹 등록
    public void add(String group_name, Integer group_size, Date group_start_date, Date group_finish_date, String group_introduce, Integer payment_no,  GameContents gameContents, Member member) {
        StudyGroups studyGroups = new StudyGroups();
        studyGroups.setGroupName(group_name);
        studyGroups.setGroupSize(group_size);
        studyGroups.setGroupStartDate(group_start_date);
        studyGroups.setGroupFinishDate(group_finish_date);
        studyGroups.setGroupIntroduce(group_introduce);
        studyGroups.setPaymentNo(payment_no);
        studyGroups.setGameContents(gameContents);
        studyGroups.setMember(member);
        //studyGroups.setGameContents(gameContentRepository.findById(game_content_no).orElse(null));
        //studyGroups.setMember(memberRepository.findById(member_no).orElse(null));
        groupRepository.save(studyGroups);
    }

    //게임콘텐츠 리스트
    public List<GameContentsListDTO> getGameList(Map<String, Object> map) {
        return studyGroupRepository.getGameList(map);
    }

    //payment_no로 그룹 지정 인원 구하기
    public int getAppointedGroupNum(int payment_no){
        return studyGroupRepository.getAppointedGroupNum(payment_no);
    }

    //게임콘텐츠 리스트 cnt
    public int getGameListCnt(int member_no){
        return studyGroupRepository.getGameListCnt(member_no);
    }

    //게임콘텐츠 리스트 조회 버튼
    public List<GameContentsListDTO> selectGame(Map<String, Integer> map) {
        return studyGroupRepository.selectGame(map);
    }

    //학습그룹 등록(게임리스트) 조회 cnt
    public int selectGameCnt(Map<String, Integer> map) {
        return studyGroupRepository.selectGameCnt(map);
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

    //학습 그룹 가입(학습그룹 리스트 전체)
    public List<GroupJoinListDTO> getGroupJoinList(Map<String, Integer> map) {
        return studyGroupRepository.getGroupJoinList(map);
    }

    //학습 그룹 가입(학습그룹 리스트 전체) cnt
    public int getGroupJoinListCnt() {
        return studyGroupRepository.getGroupJoinListCnt();
    }

    //학습그룹명 조건 조회(학습 그룹 신청 리스트)
    public List<GroupJoinListDTO> selectNameList(Map<String, Integer> map) {
        return studyGroupRepository.selectNameList(map);
    }

    //교육자명 조건 조회(학습 그룹 신청 리스트)
    public List<GroupJoinListDTO> SelectEducatorNameList(Map<String, Integer> map) {
        return studyGroupRepository.SelectEducatorNameList(map);
    }

    //교육자명 조건 조회(학습 그룹 신청 리스트) cnt
    public int SelectEducatorNameListCnt(int member_no) {
        return studyGroupRepository.SelectEducatorNameListCnt(member_no);
    }

    //학습그룹명&교육자명 조건 조회(학습 그룹 신청 리스트)
    public List<GroupJoinListDTO> SelectGroupJoinList(Map<String, Integer> map) {
        return studyGroupRepository.SelectGroupJoinList(map);
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
    public List<ApproveListDTO> getApproveList(Map<String, Integer> map) {
        return studyGroupRepository.getApproveList(map);
    }

    //학습그룹 승인 신청
    public int updateApproval(List<Integer> groupStudentsNoList){
        return studyGroupRepository.updateApproval(groupStudentsNoList);
    }

    //게임콘텐츠 정보 리스트
    public GameContentsListDTO getGameInfo(Map<String, Integer> map) {
        return studyGroupRepository.getGameInfo(map);
    }

    //학습그룹 등록학생수 가져오기(학습그룹 등록 폼)
    public int getGroupNum(int group_no) {
        return studyGroupRepository.getGroupNum(group_no);
    }

    //학습그룹명 리스트 가져오기
    public List<HashMap<String, Object>> getGroupName(int member_no){
        return studyGroupRepository.getGroupName(member_no);
    }

    //게임콘텐츠명 리스트 가져오기
    public List<HashMap<String, Object>> getGameName(int member_no){
        return studyGroupRepository.getGameName(member_no);
    }

    //학습그룹명 리스트(전체) 가져오기
    public List<HashMap<String, Object>> getGroupNameALL(){
        return studyGroupRepository.getGroupNameALL();
    }

    //교육자명 리스트(전체) 가져오기
    public List<HashMap<String, Object>> getEducatorName(){
        return studyGroupRepository.getEducatorName();
    }

    //학습 그룹 신청 리스트에서 기본으로 보여질 group_no
    public int basicGroupNo(int member_no){
        return studyGroupRepository.basicGroupNo(member_no);
    }

    //학습 그룹 가입 확인(학생)
    public JoinConfirmDTO joinConfirm(int member_no) {
        return studyGroupRepository.joinConfirm(member_no);
    }

    //학습 그룹 가입 이력 확인(학생)
    public List<JoinConfirmDTO> joinRecord(int member_no) {
        return studyGroupRepository.joinRecord(member_no);
    }

    //학습 그룹 가입 취소(학생)
    public void cancel(GroupStudents groupStudents) {
        groupStudentsRepository.delete(groupStudents);
    }

    //그룹 학생 가져오기
    public GroupStudents getGroupStudents(int group_students_no){
        Optional<GroupStudents> groupStudents = groupStudentsRepository.findById(group_students_no);
        return groupStudents.get();
    }

    //진행 중 학습 그룹 확인
    public int ingStudyGroup(int member_no) {
        return studyGroupRepository.ingStudyGroup(member_no);
    }

}
