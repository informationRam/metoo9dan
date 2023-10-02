package com.idukbaduk.metoo9dan.studyGroup.repository;

import com.idukbaduk.metoo9dan.studyGroup.dto.GameContentsListDTO;
import com.idukbaduk.metoo9dan.studyGroup.dto.GroupsDetailListDTO;
import com.idukbaduk.metoo9dan.studyGroup.dto.StudyGroupsListDTO;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StudyGroupRepository {
    private final SqlSession sqlSession;

    public List<StudyGroupsListDTO> getGroupList(int member_no) {
        return sqlSession.selectList("StudyGroup.StudyGroupList",member_no);
    }

    public List<GroupsDetailListDTO> getGroupDetailList(int group_no){
        return sqlSession.selectList("StudyGroup.GroupMemberList",group_no);
    }
    public List<GroupsDetailListDTO> getGroupInfo(int group_no){
        return sqlSession.selectList("StudyGroup.GroupInfo",group_no);
    }

    public List<GameContentsListDTO> getGameList(int member_no) {
        return sqlSession.selectList("StudyGroup.GameContentList",member_no);
    }
}
