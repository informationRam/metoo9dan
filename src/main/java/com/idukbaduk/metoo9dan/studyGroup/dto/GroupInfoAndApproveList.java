package com.idukbaduk.metoo9dan.studyGroup.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupInfoAndApproveList {

    private List<GroupsDetailListDTO> groupInfo;
    private List<ApproveListDTO> approveList;

    public GroupInfoAndApproveList(List<GroupsDetailListDTO> groupInfo, List<ApproveListDTO> approveList) {
        this.groupInfo = groupInfo;
        this.approveList = approveList;
    }
}
