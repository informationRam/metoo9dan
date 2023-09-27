package com.idukbaduk.metoo9dan.studyGroup.controller;

import com.idukbaduk.metoo9dan.studyGroup.dto.StudyGroupsListDTO;
import com.idukbaduk.metoo9dan.studyGroup.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/studygroup")
@Controller
@RequiredArgsConstructor
public class StudyGroupController {
    private final StudyGroupService studyGroupService;
    //학습 그룹 등록(교육자)

    //학습 그룹 등록 상세(교육자)

    //학습 그룹 목록 조회(교육자)
    @GetMapping("/list")
    public String studygroupList(Model model,@RequestParam(value ="page",defaultValue="0") int page){
        List<StudyGroupsListDTO> studyGroupPage = this.studyGroupService.getList(page);
        model.addAttribute("studyGroupPage",studyGroupPage);
        return "";
    }

    //학습 그룹 상세 조회(교육자)

    //학습 그룹 가입 신청(학생)

    //학습 그룹 가입 확인(학생)
}
