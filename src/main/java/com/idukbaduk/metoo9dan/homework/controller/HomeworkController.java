package com.idukbaduk.metoo9dan.homework.controller;

import com.idukbaduk.metoo9dan.common.entity.*;
import com.idukbaduk.metoo9dan.homework.domain.GroupStudentDTO;
import com.idukbaduk.metoo9dan.homework.domain.HomeworkDTO;
import com.idukbaduk.metoo9dan.homework.domain.HomeworkSubmitDetailDTO;
import com.idukbaduk.metoo9dan.homework.service.HomeworkService;
import com.idukbaduk.metoo9dan.homework.validation.HomeworksForm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/homework")
public class HomeworkController {

    @Autowired
    private HomeworkService homeworkService;


    //숙제 생성 페이지 ****모달창에서 수정/삭제 추가해야함****
    @GetMapping("/add")
    public String showCreateForm(Model model, HomeworksForm homeworksForm) {
        //멤버 아이디 로그인한 principle로 바꿔야함!!!!!!!!!!!!!
        List<HomeworkDTO> homework =homeworkService.findAllHomeworkWithSendStatus("baduk");
        model.addAttribute("homeworkList",homework);
        return "homework/homework_add";
    }

    @GetMapping("/detail/{homeworkId}")
    public ResponseEntity<HomeworkDTO> getHomeworkDetail(@PathVariable Integer homeworkId) {
        Homeworks homework=homeworkService.findById(homeworkId);
        homeworkService.getHomeworkSendListByhomework(homeworkId);

        HomeworkDTO detail = new HomeworkDTO();
        detail.setHomeworkTitle(homework.getHomeworkTitle());
        detail.setHomeworkContent(homework.getHomeworkContent());
        detail.setProgress(homework.getProgress());
        detail.setHomeworkMemo(homework.getHomeworkMemo());
        detail.setDueDate(homework.getDueDate());
        detail.setCreationDate(homework.getCreationDate());

        //여기서 detail 말고 다른 객체도 생성해서 보내주고싶어

        return ResponseEntity.ok(detail);
    }
    @PostMapping("/add")
    public String createHomework(@Valid HomeworksForm homeworksForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            //멤버 아이디 로그인한 principle로 바꿔야함!!!!!!!!!!!!!
            model.addAttribute("homeworkList", homeworkService.findAllHomeworkWithSendStatus("baduk"));
            return "homework/homework_add";
        }
        //멤버 아이디 로그인한 principle로 바꿔야함!!!!!!!!!!!!!
        Member member = homeworkService.findMemberByMemberId("baduk");
        homeworkService.saveHomework(homeworksForm,member);
        return "redirect:/homework/add";
    }

    @GetMapping("/send")
    public String showSendPage(Model model) {
        //로그인한 아이디로 바꿔야함!!!!!!!!!!!!!!!!!!!!!!
        //멤버 아이디로 생성한 숙제중에 제출기한이 지나지 않은 숙제를 가져옴 이거 디티오에 넣어줘야함(미리보기용)
        List<Homeworks> homeworks = homeworkService.findHomeworksByMemberIdAndDueDateAfter("baduk");
        List<HomeworkDTO> homeworkDTO = homeworkService.toHomeworkDTOList(homeworks);
        List<StudyGroups> studyGroups = homeworkService.findStudyGroupsByMemberId("baduk");

        List<String> distinctTitles = homeworks.stream()
                .map(Homeworks::getHomeworkTitle)
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("homeworks", homeworkDTO);
        model.addAttribute("studyGroups", studyGroups);
        model.addAttribute("distinctHomeworkTitles",distinctTitles);
        return "homework/homework_send";
    }

    @GetMapping("/group-students")
    public ResponseEntity<List<GroupStudentDTO>> getStrudentListByGroupNo(@RequestParam int studyGroupNo) {
        List<GroupStudentDTO> groupStudentDTOS = homeworkService.getGroupStudentsWithLatestProgress(studyGroupNo);
        return ResponseEntity.ok(groupStudentDTOS);
    }

    @PostMapping("/send")
    public String sendHomework(@RequestParam List<String> selectedHomeworks,
                               @RequestParam List<String> selectedMembers,
                               RedirectAttributes redirectAttributes) {
        List<String> skipped = homeworkService.saveHomeworksForMembers(selectedHomeworks, selectedMembers);
        redirectAttributes.addFlashAttribute("skippedEntries", skipped); // 건너뛴 학생과 숙제의 조합 정보를 리디렉션 후에 사용할 수 있게 저장
        redirectAttributes.addFlashAttribute("successMessage", "숙제가 전송되었습니다");
        return "redirect:/homework/send";
    }

    //숙제 제출 화면
    @GetMapping("/submit")
    public String getHomeworkList(Model model) {
        //아이디로 전송된 숙제 찾기
        List<HomeworkSend> homeworkSendList = homeworkService.findHomeworkSendByMemberId("sedol");
        model.addAttribute("homeworkSend", homeworkSendList);
        return "homework/homework_submit";
    }

    //숙제 더블클릭 시, 전송할 객체
    @GetMapping("/submit/detail/{sendNo}")
    public ResponseEntity<HomeworkSubmitDetailDTO> getHomeworkSubmitDetail(@PathVariable Integer sendNo) {
        HomeworkSubmitDetailDTO dto = homeworkService.getDetail(sendNo);
        return ResponseEntity.ok(dto);
    }
    //숙제 제출 post
    //@PostMapping("/submit/add")
    //public ResponseEntity<?> homeworkAdd(@RequestBody HomeworkSubmitDTO homeworkSubmitDto){
    //}


    //숙제 수정 post
    //@PostMapping("/submit/edit")
    //숙제 삭제 post

    //숙제 평가 보기

    //평가하기 페이지

    //평가 저장 페이지

    //평가 저장 post
}
