package com.idukbaduk.metoo9dan.education.controller;

import com.idukbaduk.metoo9dan.education.service.EducationalService;
import com.idukbaduk.metoo9dan.education.vaildation.EducationVaildation;
import com.idukbaduk.metoo9dan.game.vaildation.GameVaildation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final EducationalService educationalService;

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
            educationalService.save(educationVaildation);
            return "redirect:/game/list";
        }
    }
}
