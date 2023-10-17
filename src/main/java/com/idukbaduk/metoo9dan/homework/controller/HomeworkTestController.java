//package com.idukbaduk.metoo9dan.homework.controller;
//
//import com.idukbaduk.metoo9dan.common.entity.HomeworkSend;
//import com.idukbaduk.metoo9dan.common.entity.Homeworks;
//import com.idukbaduk.metoo9dan.homework.domain.HomeworkSendDTO;
//import com.idukbaduk.metoo9dan.homework.domain.HwSendSubmitDTO;
//import com.idukbaduk.metoo9dan.homework.service.HomeworkService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.time.LocalDateTime;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Controller
//@RequestMapping("/homework") //원래 api였음
//public class HomeworkTestController {
//
//    @Autowired
//    private HomeworkService homeworkService;
//
//    @GetMapping("/evaluate")
//    public String evaluateView(Model model){
//        //숙제 전송 리스트의 숙제중에 baduk 아이디로 만들어진 숙제 전송 기록 중에 Homeworks를 리스트로 가져온다
//        List<Homeworks> homeworks = homeworkService.findHomeworksByMemberId("baduk");
//        List<String> distinctTitles = homeworks.stream()
//                .map(Homeworks::getHomeworkTitle)
//                .distinct()
//                .collect(Collectors.toList());
//        model.addAttribute("titles",distinctTitles);
//        return "homework/homework_evaluate";
//    }
//    @GetMapping("/evaluate/hw-list")
//    public ResponseEntity<?> getHomeworks(
//            @RequestParam(defaultValue = "All") String title,
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "5") int size,
//            @RequestParam(defaultValue = "asc") String sort
//    ) {
//        page=page-1;
//        Pageable pageable = PageRequest.of(page, size, sort.equals("asc") ? Sort.by("sendDate").ascending() : Sort.by("sendDate").descending());
//        Page<HomeworkSendDTO> homeworksDto = homeworkService.findByHomeworks_HomeworkTitleAndHomeworks_Member_MemberId(title, "baduk", pageable);
//        return ResponseEntity.ok(homeworksDto);
//    }
//
//
//    @GetMapping("/evaluate/submit-list")
//    public ResponseEntity<?> getHomeworks(/*homeworkNo SendDate SendNo*/
//            @RequestParam int homeworkNo,
//            @RequestParam int sendNo,
//            @RequestParam LocalDateTime sendDate
//    ) {
//        //1. homeworkService찾기
//        List<HomeworkSend> homeworkSendList =homeworkService.findAllBySendDateAndHomeworks_HomeworkNo(homeworkNo,sendDate);
//        System.out.println(homeworkSendList);
//        //DTO순환하면서 homeworkSubmit 찾고, 없으면 혼자 DTO변환 있으면 같이 DTO변환해서 리스트에 추가
//        List<HwSendSubmitDTO> submitDTO = homeworkService.toSubmitDTO(homeworkSendList);
//        //결과로 반환
//        return ResponseEntity.ok(submitDTO);
//    }
//}