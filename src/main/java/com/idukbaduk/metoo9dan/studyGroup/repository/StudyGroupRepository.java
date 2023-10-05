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

    //학습 그룹 가입(학습그룹 리스트 전체)
    public List<GroupJoinListDTO> getGroupJoinList() {

        return sqlSession.selectList("StudyGroup.GroupJoinList");
    }

    //학습 그룹 신청 리스트
    public List<ApproveListDTO> getApproveList(int member_no) {
        return sqlSession.selectList("StudyGroup.ApproveList",member_no);
    }

    //학습 그룹 승인 처리
    public int updateApproval(List<Integer> groupStudentsNoList){
        return sqlSession.update("StudyGroup.updateApproval",groupStudentsNoList); //수정 성공시 1반환
    }

    //학습그룹명 리스트 가져오기
    public List<HashMap<String, Object>> getGroupName(int member_no){
        return sqlSession.selectList("StudyGroup.GroupNameList",member_no);
    }



}
