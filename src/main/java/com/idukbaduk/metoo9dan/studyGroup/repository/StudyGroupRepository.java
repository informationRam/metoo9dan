package com.idukbaduk.metoo9dan.studyGroup.repository;

import com.idukbaduk.metoo9dan.studyGroup.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StudyGroupRepository {
    private final SqlSession sqlSession;

    //학습그룹 리스트
    public List<StudyGroupsListDTO> getGroupList(int member_no) {
        return sqlSession.selectList("StudyGroup.StudyGroupList",member_no);
    }

    //학습그룹 리스트(페이지네이션)
    public List<StudyGroupsListDTO> getGroupListPage(Map<String, Integer> map) {
        return sqlSession.selectList("StudyGroup.StudyGroupListPage",map);
    }

    //학습그룹 리스트 Cnt
    public int getGroupListCnt(int member_no) {
        return sqlSession.selectOne("StudyGroup.StudyGroupListCnt",member_no);
    }

    //학습그룹 리스트 조회 버튼
    public List<StudyGroupsListDTO> selectGroup(Map<String, Integer> map) {
        return sqlSession.selectList("StudyGroup.GroupSelect",map);
    }


    //학습그룹 상세조회(그룹 멤버)
    public List<GroupsDetailListDTO> getGroupDetailList(int group_no){
        return sqlSession.selectList("StudyGroup.GroupMemberList",group_no);
    }

    //학습그룹 상세조회(그룹 정보)
    public List<GroupsDetailListDTO> getGroupInfo(int group_no){
        return sqlSession.selectList("StudyGroup.GroupInfo",group_no);
    }

    //학습그룹 등록(게임리스트)
    public List<GameContentsListDTO> getGameList(Map<String, Object> map) {
        return sqlSession.selectList("StudyGroup.GameContentList",map);
    }

    //payment_no로 그룹 지정 인원 구하기
  /*  public int getAppointedGroupNum(int payment_no){
        return sqlSession.selectOne("StudyGroup.AppointedGroupNum",payment_no);
    }*/
    public Integer getAppointedGroupNum(int payment_no) {
        Integer appointedGroupNum = sqlSession.selectOne("StudyGroup.AppointedGroupNum", payment_no);
        return appointedGroupNum != null ? appointedGroupNum : 0; // 기본값 설정
    }

    //게임콘텐츠 리스트 cnt
    public int getGameListCnt(int member_no){
        return sqlSession.selectOne("StudyGroup.GameContentListCnt",member_no);
    }

    //학습그룹 등록(게임리스트) 조회 버튼
    public List<GameContentsListDTO> selectGame(Map<String, Integer> map) {
        return sqlSession.selectList("StudyGroup.SelectGameList",map);
    }

    //학습그룹 등록(게임리스트) 조회 cnt
    public int selectGameCnt(Map<String, Integer> map) {
        return sqlSession.selectOne("StudyGroup.SelectGameListCnt",map);
    }

    //학습 그룹 가입(학습그룹 리스트 전체)
    public List<GroupJoinListDTO> getGroupJoinList(Map<String, Integer> map) {
        return sqlSession.selectList("StudyGroup.GroupJoinList",map);
    }

    //학습 그룹 가입(학습그룹 리스트 전체) cnt
    public int getGroupJoinListCnt() {
        return sqlSession.selectOne("StudyGroup.GroupJoinListCnt");
    }

    //학습그룹명 조건 조회(학습 그룹 신청 리스트)
    public List<GroupJoinListDTO> selectNameList(Map<String, Integer> map) {
        return sqlSession.selectList("StudyGroup.SelectNameList",map);
    }

    //교육자명 조건 조회(학습 그룹 신청 리스트)
    public List<GroupJoinListDTO> SelectEducatorNameList(Map<String, Integer> map) {
        return sqlSession.selectList("StudyGroup.SelectEducatorNameList",map);
    }

    //교육자명 조건 조회(학습 그룹 신청 리스트) cnt
    public int SelectEducatorNameListCnt(int member_no) {
        return sqlSession.selectOne("StudyGroup.SelectEducatorNameListCnt",member_no);
    }

    //학습그룹명&교육자명 조건 조회(학습 그룹 신청 리스트)
    public List<GroupJoinListDTO> SelectGroupJoinList(Map<String, Integer> map) {
        return sqlSession.selectList("StudyGroup.SelectGroupJoinList",map);
    }

    //학습 그룹 신청 리스트
    public List<ApproveListDTO> getApproveList(Map<String, Integer> map) {
        return sqlSession.selectList("StudyGroup.ApproveList",map);
    }

    //학습 그룹 승인 처리
    public int updateApproval(List<Integer> groupStudentsNoList){
        return sqlSession.update("StudyGroup.updateApproval",groupStudentsNoList); //수정 성공시 1반환
    }


    //게임콘텐츠 정보 리스트 가져오기(학습그룹 등록 폼)
    public GameContentsListDTO getGameInfo(Map<String, Integer> map) {
        return sqlSession.selectOne("StudyGroup.GameContentInfo",map);
    }

    //학습그룹 등록학생수 가져오기(학습그룹 등록 폼)
    public int getGroupNum(int group_no) {
        return sqlSession.selectOne("StudyGroup.approved_num",group_no);
    }

    //학습그룹명 리스트 가져오기
    public List<HashMap<String, Object>> getGroupName(int member_no){
        return sqlSession.selectList("StudyGroup.GroupNameList",member_no);
    }

    //게임콘텐츠명 리스트 가져오기
    public List<HashMap<String, Object>> getGameName(int member_no){
        return sqlSession.selectList("StudyGroup.GameNameList",member_no);
    }

    //학습그룹명 리스트(전체) 가져오기
    public List<HashMap<String, Object>> getGroupNameALL(){
        return sqlSession.selectList("StudyGroup.GroupNameAllList");
    }

    //교육자명 리스트(전체) 가져오기
    public List<HashMap<String, Object>> getEducatorName(){
        return sqlSession.selectList("StudyGroup.EducatorNameList");
    }

    //학습 그룹 신청 리스트에서 기본으로 보여질 group_no
    public int basicGroupNo(int member_no){
        Integer result = sqlSession.selectOne("StudyGroup.BasicGroupNo",member_no);
        return result != null ? result : 0; //result이 null일 때 예외처리
    }

    //학습 그룹 가입 확인(학생)
    public JoinConfirmDTO joinConfirm(int member_no) {
        return sqlSession.selectOne("StudyGroup.JoinConfirm",member_no);
    }

    //학습 그룹 가입 이력 확인(학생)
    public List<JoinConfirmDTO> joinRecord(int member_no) {
        return sqlSession.selectList("StudyGroup.JoinRecord",member_no);
    }

    //진행 중 학습 그룹 확인
    public int ingStudyGroup(int member_no) {
        return sqlSession.selectOne("StudyGroup.IngStudyGroup",member_no);
    }
}
