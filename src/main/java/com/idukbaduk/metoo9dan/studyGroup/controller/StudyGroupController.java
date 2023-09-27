package com.idukbaduk.metoo9dan.studyGroup.controller;

import com.idukbaduk.metoo9dan.studyGroup.dto.GroupsDetailListDTO;
import com.idukbaduk.metoo9dan.studyGroup.dto.StudyGroupsListDTO;
import com.idukbaduk.metoo9dan.studyGroup.service.StudyGroupService;
import com.idukbaduk.metoo9dan.studyGroup.validation.StudyGroupForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/studygroup")
@Controller
@RequiredArgsConstructor
public class StudyGroupController {
    private final StudyGroupService studyGroupService;
    //학습 그룹 등록(교육자)


    //학습 그룹 등록 상세(교육자)
    //학습 그룹 폼
    @GetMapping("/add")
    public String add(StudyGroupForm studyGroupForm){
        return "studyGroup/studyGroup_form";
    }

    //학습 그룹 등록 처리
    @PostMapping("/add")
    public String studygroupAdd(@Valid StudyGroupForm studyGroupForm){

        return "";
    }

    //학습 그룹 목록 조회(교육자)
    @GetMapping("/list")
    public String studygroupList(Model model,@RequestParam(defaultValue="10") int member_no){
        List<StudyGroupsListDTO> studyGroup = studyGroupService.getList(member_no);
        model.addAttribute("studyGroup",studyGroup);
        System.out.println("studyGroup="+studyGroup);
        return "studyGroup/studyGroup_list";
    }


    //학습 그룹 상세 조회(교육자)
    @GetMapping("/detail/{group_no}")
    public String groupDetail(Model model, @PathVariable("group_no") int group_no){
        List<GroupsDetailListDTO> GroupDetail = studyGroupService.getDetailList(group_no);
        List<GroupsDetailListDTO> GroupInfo = studyGroupService.getGroupInfo(group_no);
        model.addAttribute("GroupDetail",GroupDetail);
        model.addAttribute("GroupInfo",GroupInfo);
        System.out.println("GroupDetail="+GroupDetail);
        System.out.println("GroupInfo="+GroupInfo);
        return "studyGroup/studyGroup_detail";
    }
  /*  @GetMapping("/detail")
    public String groupDetail(Model model,@RequestParam(defaultValue="1") int group_no){
        List<GroupsDetailListDTO> GroupDetail = studyGroupService.getDetailList(group_no);
        model.addAttribute("GroupDetail",GroupDetail);
        System.out.println("GroupDetail="+GroupDetail);
      return "studyGroup/studyGroup_detail";
    }*/


    //학습 그룹 가입 신청(학생)


    //학습 그룹 가입 확인(학생)



}
