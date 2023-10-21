package com.idukbaduk.metoo9dan.homework.controller;

import com.idukbaduk.metoo9dan.common.entity.*;
import com.idukbaduk.metoo9dan.homework.domain.*;
import com.idukbaduk.metoo9dan.homework.validation.HwSubmitForm;
import com.idukbaduk.metoo9dan.homework.service.HomeworkService;
import com.idukbaduk.metoo9dan.homework.validation.HomeworksEditForm;
import com.idukbaduk.metoo9dan.homework.validation.HomeworksForm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/homework")
public class HomeworkController {

    @Autowired
    private HomeworkService homeworkService;

    //--생성 페이지-------------------------------------------------------------------------------------------------------

    //숙제 생성 페이지 보여주기
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @GetMapping("/add")
    public String showCreateForm(Model model, HomeworksForm homeworksForm, HomeworksEditForm homeworksEditForm) {
        //숙제 테이블 list객체
        //멤버 아이디 로그인한 principle로 바꿔야함!!!!!!!!!!!!!
        List<HomeworkDTO> homework =homeworkService.findAllHomeworkWithSendStatus("jung123");
        List<String> titles = homeworkService.findGameContentTitlesByMemberId("jung123");
        model.addAttribute("homeworkList",homework);
        model.addAttribute("titles",titles);
        return "homework/homework_add";
    }

    //숙제 생성 폼 제출
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @PostMapping("/add")
    public String createHomework(@Valid HomeworksForm homeworksForm, BindingResult result, Model model, HomeworksEditForm homeworksEditForm) {
        //유효성 검사
        if (result.hasErrors()) {
            model.addAttribute("homeworkList", homeworkService.findAllHomeworkWithSendStatus("jung123"));
            List<String> titles = homeworkService.findGameContentTitlesByMemberId("jung123");
            model.addAttribute("homeworksEditForm", homeworksEditForm);
            model.addAttribute("titles",titles);
            return "homework/homework_add";
        }

        //만든 숙제 저장
        //멤버 아이디 로그인한 principle로 바꿔야함!!!!!!!!!!!!!
        Member member = homeworkService.findMemberByMemberId("jung123");
        homeworkService.saveHomework(homeworksForm,member);
        return "redirect:/homework/add";
    }

    //숙제 상세 정보 보내주기
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @GetMapping("/detail/{homeworkId}")
    public ResponseEntity<HomeworkDTO> getHomeworkDetail(@PathVariable Integer homeworkId) {
        Homeworks homework=homeworkService.findById(homeworkId);
        HomeworkDTO detail = homeworkService.convertToDTO(homework);
        return ResponseEntity.ok(detail);
    }


    //숙제 수정 폼 제출
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @PostMapping("/edit")
    public ResponseEntity<?> editHomework(@Valid HomeworksEditForm homeworksEditForm, BindingResult result, Model model, HomeworksForm homeworksForm) {
        if (result.hasErrors()) {
            // 에러 메시지를 담을 Map 생성
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                String fieldName = error.getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            // 400 상태 코드와 함께 에러 메시지를 JSON 형태로 반환
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        homeworkService.updateHomework(homeworksEditForm,homeworkService.getHomeworkById(homeworksEditForm.getHwNo()).get());

        // 성공 응답 (HTTP 상태 코드 200 OK와 함께 메시지 반환)
        return ResponseEntity.ok("Homework edited successfully");
    }

    //숙제 삭제
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteHomework(@PathVariable Integer id) {
        try {
            System.out.println(id);
            homeworkService.deleteHomework(id);
            return new ResponseEntity<>("삭제 성공", HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>("삭제 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //--전송 페이지-------------------------------------------------------------------------------------------------------

    //전송 초기 화면
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @GetMapping("/send")
    public String showSendPage(Model model) {
        //로그인한 아이디로 바꿔야함!!!!!!!!!!!!!!!!!!!!!!
        //멤버 아이디로 생성한 숙제중에 제출기한이 지나지 않은 숙제를 가져옴 이거 디티오에 넣어줘야함(미리보기용)
        List<Homeworks> homeworks = homeworkService.findHomeworksByMemberIdAndDueDateAfter("jung123");
        List<HomeworkDTO> homeworkDTO = homeworkService.toHomeworkDTOList(homeworks);
        List<StudyGroups> studyGroups = homeworkService.findStudyGroupsByMemberId("jung123");

        List<String> distinctTitles = new ArrayList<>();
        for (Homeworks homework : homeworks) {
            String title = homework.getHomeworkTitle();
            if (!distinctTitles.contains(title)) {
                distinctTitles.add(title);
            }
        }
        model.addAttribute("homeworks", homeworkDTO);
        model.addAttribute("studyGroups", studyGroups);
        model.addAttribute("distinctHomeworkTitles",distinctTitles);
        return "homework/homework_send";
    }

    //학생 그룹 조회
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @GetMapping("/group-students")
    public ResponseEntity<List<GroupStudentDTO>> getStrudentListByGroupNo(@RequestParam int studyGroupNo) {
        List<GroupStudentDTO> groupStudentDTOS = homeworkService.getGroupStudentsWithLatestProgress(studyGroupNo);
        return ResponseEntity.ok(groupStudentDTOS);
    }

    //숙제 전송
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @PostMapping("/send")
    public String sendHomework(@RequestParam List<String> selectedHomeworks,
                               @RequestParam List<String> selectedMembers,
                               RedirectAttributes redirectAttributes) {
        List<String> skipped = homeworkService.saveHomeworksForMembers(selectedHomeworks, selectedMembers);
        redirectAttributes.addFlashAttribute("skippedEntries", skipped); // 건너뛴 학생과 숙제의 조합 정보를 리디렉션 후에 사용할 수 있게 저장
        redirectAttributes.addFlashAttribute("successMessage", "숙제가 전송되었습니다");
        return "redirect:/homework/send";
    }

    //숙제 전송 취소
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @DeleteMapping("/send/cancel/{sendNo}")
    public ResponseEntity<Map<String, Boolean>> sendCancel(@PathVariable Integer sendNo) {
        try {
            homeworkService.deleteHomeworkSend(sendNo);
            Map<String, Boolean> response = new HashMap<>();
            response.put("success", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // 에러 핸들링
            Map<String, Boolean> response = new HashMap<>();
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //--학생 숙제 페이지-------------------------------------------------------------------------------------------------------

    //숙제 초기 화면
    //@PreAuthorize("hasAuthority('STUDENT ')")
    @GetMapping("/submit")
    public String getHomeworkList(HwSubmitForm hwSubmitForm, Model model) {
        //아이디로 전송된 숙제 찾기 - 로그인 정보로 바꿔야함!!!!!!!!!!!
        List<HomeworkSend> homeworkSendList = homeworkService.findHomeworkSendByMemberId("sedol");
        model.addAttribute("homeworkSend", homeworkSendList);
        return "homework/homework_submit";
    }

    //숙제 더블클릭 시, 전송할 객체
    //@PreAuthorize("hasAuthority('STUDENT ')")
    @GetMapping("/submit/detail/{sendNo}")
    public ResponseEntity<HomeworkSubmitDetailDTO> getHomeworkSubmitDetail(@PathVariable Integer sendNo) {
        HomeworkSubmitDetailDTO dto = homeworkService.getDetail(sendNo);
        return ResponseEntity.ok(dto);
    }

    //숙제 제출 post
    //@PreAuthorize("hasAuthority('STUDENT ')")
    @PostMapping("/submit/add")
    public ResponseEntity<?> submitHomework(@Valid HwSubmitForm hwSubmitForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // 에러 메시지를 담을 Map 생성
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                String fieldName = error.getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
                System.out.println(errors);
            });
            // 400 상태 코드와 함께 에러 메시지를 JSON 형태로 반환
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        System.out.println("폼에서 받은 것들"+hwSubmitForm);
        Homeworks homeworks =homeworkService.findById(hwSubmitForm.getHomeworkNo());
        HomeworkSend homeworkSend = homeworkService.getHomeworkSendById(hwSubmitForm.getSendNo()).get();//전송 번호가 null
        //로그인한 아이디로 바꿔야함!!!!!!!!!!!!!!!!!
        hwSubmitForm.setMember(homeworkService.findMemberByMemberId("baduk"));
        homeworkService.addHomeworkSubmit(hwSubmitForm,homeworks,homeworkSend);

        // 성공 응답 (HTTP 상태 코드 200 OK와 함께 메시지 반환)
        return ResponseEntity.ok("Homework edited successfully");
    }

    //숙제 수정 post
    //@PreAuthorize("hasAuthority('STUDENT ')")
    @PostMapping("/submit/edit")
    public ResponseEntity<?> editSubmitHomework(@Valid HwSubmitForm hwSubmitForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // 에러 메시지를 담을 Map 생성
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                String fieldName = error.getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
                System.out.println(errors);
            });
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        HomeworkSubmit homeworkSubmit =homeworkService.getHomeworkSubmitByHomeworkSubmitNo(hwSubmitForm.getHomeworkSubmitNo());
        //로그인한 아이디로 바꿔야함!!!!!!!!!!!!!!!!!
        homeworkService.updateHomeworkSubmitted(hwSubmitForm,homeworkSubmit);

        // 성공 응답 (HTTP 상태 코드 200 OK와 함께 메시지 반환)
        return ResponseEntity.ok("Homework edited successfully");
    }

    //숙제 삭제 post
    //@PreAuthorize("hasAuthority('STUDENT ')")
    @DeleteMapping("/submit/delete/{id}")
    public ResponseEntity<String> deleteHomeworkSubmitted(@PathVariable Integer id) {
        try {
            homeworkService.deleteHomeworkSubmitted(id);
            return new ResponseEntity<>("삭제 성공", HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>("삭제 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //--평가 페이지-------------------------------------------------------------------------------------------------------

    //숙제 평가
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @GetMapping("/evaluate")
    public String evaluateView(Model model){
        //숙제 전송 리스트의 숙제중에 baduk 아이디로 만들어진 숙제 전송 기록 중에 Homeworks를 리스트로 가져온다
        List<Homeworks> homeworks = homeworkService.findHomeworksByMemberId("jung123");
        List<String> distinctTitles = new ArrayList<>();
        for (Homeworks homework : homeworks) {
            String title = homework.getHomeworkTitle();
            if (!distinctTitles.contains(title)) {
                distinctTitles.add(title);
            }
        }
        model.addAttribute("titles",distinctTitles);
        return "homework/homework_evaluate";
    }

    //전송한 숙제 리스트
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @GetMapping("/evaluate/hw-list")
    public ResponseEntity<?> getHomeworks(
            @RequestParam(defaultValue = "All") String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "asc") String sort
    ) {
        page=page-1;
        Pageable pageable = PageRequest.of(page, size, sort.equals("asc") ? Sort.by("sendDate").ascending() : Sort.by("sendDate").descending());
        Page<HomeworkSendDTO> homeworksDto = homeworkService.findByHomeworks_HomeworkTitleAndHomeworks_Member_MemberId(title, "jung123", pageable);
        return ResponseEntity.ok(homeworksDto);
    }

    //전송 내역
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @GetMapping("/evaluate/submit-list")
    public ResponseEntity<?> getHomeworks(/*homeworkNo SendDate SendNo*/
            @RequestParam int homeworkNo,
            @RequestParam String sendDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sort
    ) {
        page-=1;
        System.out.println(homeworkNo);
        System.out.println(sendDate);
        // 스트링 형식의 날짜를 LocalDateTime 객체로 변환
        LocalDateTime sd;
        try {
            sd = LocalDateTime.parse(sendDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("날짜 형식이 잘못됨");//여기서 반환되는데
        }

        //1. homeworkService찾기
        List<HomeworkSend> homeworkSendList =homeworkService.findAllBySendDateAndHomeworks_HomeworkNo(homeworkNo,sd);
        System.out.println("1차"+homeworkSendList);
        //DTO순환하면서 homeworkSubmit 찾고, 없으면 혼자 DTO변환 있으면 같이 DTO변환해서 리스트에 추가
        List<HwSendSubmitDTO> submitDTO = homeworkService.toSubmitDTO(homeworkSendList);
        //페이지네이션
        Pageable pageable = PageRequest.of(page, size, sort.equals("asc") ? Sort.by("dueDate").ascending() : Sort.by("dueDate").descending());
        Page<HwSendSubmitDTO> submitDTOPage = new PageImpl<>(submitDTO, pageable, submitDTO.size());
        //결과로 반환
        return ResponseEntity.ok(submitDTOPage);
    }

    //평가 대시보드 내용 - 제출
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @GetMapping("/evaluate/dash-submit/{homeworkNo}/{sendDate}")
    public ResponseEntity<?> evalDashboard1(
            @PathVariable int homeworkNo,
            @PathVariable String sendDate
    ){

        // 스트링 형식의 날짜를 LocalDateTime 객체로 변환
        LocalDateTime sd;
        try {
            sd = LocalDateTime.parse(sendDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("날짜 형식이 잘못됨");
        }

        List<HomeworkSend> homeworkSendList =homeworkService.findAllBySendDateAndHomeworks_HomeworkNo(homeworkNo,sd);
        int sendCnt = homeworkSendList.size();
        List<HomeworkSubmit> homeworkSubmitList = homeworkService.findSubmitsBySendNo(homeworkSendList);
        int submitCnt = homeworkSubmitList.size();

        double submitPercent=((double)submitCnt/sendCnt)*100;
        System.out.println(submitPercent);
        return ResponseEntity.ok(submitPercent);
    }

    //평가 대시보드 내용 - 평가
    @GetMapping("/evaluate/dash-eval/{homeworkNo}/{sendDate}")
    public ResponseEntity<?> evalDashboard2(
            @PathVariable int homeworkNo,
            @PathVariable LocalDateTime sendDate
    ){
        List<HomeworkSend> homeworkSendList =homeworkService.findAllBySendDateAndHomeworks_HomeworkNo(homeworkNo,sendDate);
        int sendCnt = homeworkSendList.size();
        List<HomeworkSubmit> homeworkSubmitList = homeworkService.findSubmitsBySendNo(homeworkSendList);
        int submitCnt = homeworkSubmitList.size();

        int aCnt = 0;
        int bCnt = 0;
        int cCnt = 0;
        int fCnt = 0;
        if(submitCnt !=0 ){
            for(HomeworkSubmit homeworkSubmit : homeworkSubmitList){
                String eval = homeworkSubmit.getEvaluation();
                if (eval == null) {
                    fCnt+=1;//평가도를 평가 안한 항목을 포함 시키는게 좋을까 안하는게 좋을까 정하고 코드 삭제 여부가 달라질듯
                    continue;
                }
                switch(eval) {//여기야
                    case "A":
                        aCnt+=1;
                        break;
                    case "B":
                        bCnt+=1;
                        break;
                    case "C":
                        cCnt+=1;
                        break;
                }
            }
            fCnt=sendCnt-submitCnt;
        } else{
            fCnt=sendCnt;
        }
        double aPer = (submitCnt != 0) ? ((double) aCnt / submitCnt)*100 : 0.0;
        double bPer = (submitCnt != 0) ? ((double) bCnt / submitCnt)*100 : 0.0;
        double cPer = (submitCnt != 0) ? ((double)cCnt/submitCnt)*100 : 0.0;
        double fPer = (submitCnt != 0) ? ((double)fCnt/submitCnt)*100 : 100.0;

        List<Double> evalPer = new ArrayList<>();
        evalPer.add(aPer);
        evalPer.add(bPer);
        evalPer.add(cPer);
        evalPer.add(fPer);

        return ResponseEntity.ok(evalPer);
    }

    //평가하기
    //@PreAuthorize("hasAuthority('EDUCATOR')")
    @PostMapping("/evaluate/submit-evaluation")
    public ResponseEntity<?> submitEvaluation(@RequestBody List<EvaluationDTO> evaluations) {

        for(int i = 0;i <= evaluations.size();i++){
            String stringSn = evaluations.get(i).getHomeworkSubmitNo();
            int SubmitNo = Integer.parseInt(stringSn);
            String evaluation = evaluations.get(i).getEvaluationValue();
            HomeworkSubmit homeworkSubmit =homeworkService.getSubmitBySendNo(SubmitNo);
            homeworkService.saveEvaluation(homeworkSubmit,evaluation);
        }

        return ResponseEntity.ok(Map.of("success", true));
    }

    //--지난 숙제 제출내용 조회 --------------------------------------------------------------------------------------------

    /*
    기본으로 모든 숙제를 보여주고 모든 숙제에 대한 평가도(chart.js 사용)가 보여진다
    게임 컨텐츠 드롭다운을 제공하고
    게임 컨텐츠를 선택하면 해당 게임 컨텐츠에 대해서 제출한 숙제와 평가도가 보여진다
    처음 화면에서는 게임 컨텐츠 드롭다운을 제공하고
    */

    //@PreAuthorize("hasAuthority('STUDENT ')")
    @GetMapping("/submit/past")
    public String pastHw(Model model){
        List<String> gameContents = homeworkService.findGameContentTitlesByMemberId("lee123"); // 모든 게임 컨텐츠를 가져옵니다.
        model.addAttribute("gameContents", gameContents); // 뷰에 게임 컨텐츠 데이터를 전달합니다.
        return "pastHw";
    }



}
