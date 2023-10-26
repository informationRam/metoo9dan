package com.idukbaduk.metoo9dan.admin.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
//회원 다중 삭제를 위한 DTO
public class DeleteMembersRequest  {

    private List<Integer> memberNos;
    }
