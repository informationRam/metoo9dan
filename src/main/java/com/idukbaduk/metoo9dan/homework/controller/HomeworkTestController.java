package com.idukbaduk.metoo9dan.homework.controller;

import com.idukbaduk.metoo9dan.common.entity.HomeworkSend;
import com.idukbaduk.metoo9dan.homework.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/homework") //원래 api였음
public class HomeworkTestController {

    @Autowired
    private HomeworkService homeworkService;

    @GetMapping("/evaluate")
    public String evaluateView(){
        return "homework/homework_evaluate";
    }

    @GetMapping("/evaluate/hw-list") //원래 data였음
    public ResponseEntity<?> fetchData(
            @RequestParam(name = "title", defaultValue = "") String title,
            @RequestParam(name = "sort", defaultValue = "asc") String sortOrder,
            @RequestParam(name = "page", defaultValue = "1") int page) {

        Pageable pageable = PageRequest.of(page - 1, 10,
                sortOrder.equals("asc") ? Sort.by("생성일").ascending() : Sort.by("생성일").descending());

        Page<HomeworkSend> results = homeworkService.fetchData(title, pageable);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/evaluate/hw-titles") //원래 titles였음
    public ResponseEntity<List<String>> fetchTitles() {
        List<String> titles = homeworkService.fetchAllTitles();
        System.out.println(titles);
        return ResponseEntity.ok(titles);//이십분만 더 버티자
    }
}