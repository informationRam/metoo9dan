package com.idukbaduk.metoo9dan.education.controller;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.education.service.EducationService;
import com.idukbaduk.metoo9dan.education.vaildation.EducationVaildation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    //교육자료 등록 폼
    @GetMapping("/addForm")
    public String gameAddForm(EducationVaildation educationVaildation, Model model) {
        model.addAttribute("educationVaildation", educationVaildation);
        return "education/addForm";
    }

    //교육자료 등록 처리
    @PostMapping("/add")
    public String gameAdd(@ModelAttribute("educationVaildation") @Valid EducationVaildation educationVaildation, @RequestParam("boardFile") MultipartFile file, BindingResult bindingResult, Model model) throws IOException {
        System.out.println("boardFile?"+educationVaildation.getBoardFile().getOriginalFilename());

        // 업로드된 파일의 확장자 확인
        String fileName = educationVaildation.getBoardFile().getOriginalFilename();

        if (fileName == null || fileName.isEmpty()) {
            educationVaildation.setBoardFile(null);
        }

        if (bindingResult.hasErrors()) {
            return "education/addForm";
        } else{
            educationService.save(educationVaildation);
            return "redirect:/education/list";
        }
    }

    //교육자료 목록조회
    @GetMapping("/list")
    public String gameList(Model model, @RequestParam(value = "page", defaultValue = "0") int page, EducationalResources educationalResources) {
        Page<EducationalResources> educationPage = this.educationService.getList(page);

        //3.Model
        model.addAttribute("educationalResources", educationalResources);
        model.addAttribute("educationPage", educationPage);
        return "education/list";
    }
}
