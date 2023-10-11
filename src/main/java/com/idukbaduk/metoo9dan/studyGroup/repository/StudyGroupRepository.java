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
    public List<GameContentsListDTO> getGameList(int member_no) {
        return sqlSession.selectList("StudyGroup.GameContentList",member_no);
    }

    //학습그룹 등록(게임리스트) 조회 버튼
    public List<GameContentsListDTO> selectGame(Map<String, Integer> map) {
        return sqlSession.selectList("StudyGroup.SelectGameList",map);
    }

    //학습 그룹 가입(학습그룹 리스트 전체)
    public List<GroupJoinListDTO> getGroupJoinList() {
        return sqlSession.selectList("StudyGroup.GroupJoinList");
    }

    //학습그룹명 조건 조회(학습 그룹 신청 리스트)
    public List<GroupJoinListDTO> selectNameList(int group_no) {
        return sqlSession.selectList("StudyGroup.SelectNameList",group_no);
    }

    //교육자명 조건 조회(학습 그룹 신청 리스트)
    public List<GroupJoinListDTO> SelectEducatorNameList(int member_no) {
        return sqlSession.selectList("StudyGroup.SelectEducatorNameList",member_no);
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
        return sqlSession.selectOne("StudyGroup.BasicGroupNo",member_no);
    }


}
