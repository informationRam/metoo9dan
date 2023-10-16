package com.idukbaduk.metoo9dan.studyGroup.controller;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.GroupStudents;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.StudyGroups;
import com.idukbaduk.metoo9dan.studyGroup.dto.*;
import com.idukbaduk.metoo9dan.studyGroup.repository.GameContentRepository;
import com.idukbaduk.metoo9dan.studyGroup.repository.GroupRepository;
import com.idukbaduk.metoo9dan.studyGroup.repository.MemberRepository;
import com.idukbaduk.metoo9dan.studyGroup.service.StudyGroupService;
import com.idukbaduk.metoo9dan.studyGroup.validation.StudyGroupForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@RequestMapping("/studygroup")
@Controller
@RequiredArgsConstructor
public class StudyGroupController {
    private final StudyGroupService studyGroupService;
    private final GameContentRepository gameContentRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;

    //학습 그룹 등록(교육자), 게임콘텐츠 리스트 조회
    @GetMapping("/gameList")
    public String gamelist(Model model){
        int member_no=1; //로그인 처리하기

        List<GameContentsListDTO> gameContents = studyGroupService.getGameList(member_no);
        model.addAttribute("gameContents",gameContents);
        System.out.println("gameContents="+gameContents);

        //게임콘텐츠명 리스트 가져오기
        List<HashMap<String, Object>> gameNameList = studyGroupService.getGameName(member_no);
        model.addAttribute("gameNameList",gameNameList);
        System.out.println("gameNameList="+gameNameList);

/*
        // max_subscribers 값을 가져오기 위해 gameContent 리스트를 순회
        for (GameContentsListDTO gameContent : gameContents) {
            int max_subscribers = gameContent.getMax_subscribers();
            int appointed_group_num = gameContent.getAppointed_group_num();

            if(max_subscribers==appointed_group_num){
                MessageDto message = new MessageDto("만들 수 있는 학습그룹 인원이 없습니다", "/studygroup/gameList", RequestMethod.GET, null);
                return showMessageAndRedirect(message,model);
            }
        }
*/
        return "studyGroup/gameContents_list";
    }

    //게임콘텐츠 조회하기 버튼 엔드포인트
    @GetMapping(value = "/gameListEndpoint", produces = "application/json")
    @ResponseBody
    public List<GameContentsListDTO> gamecontentsList(@RequestParam int game_content_no, @RequestParam Map<String, Integer> map) {
        int member_no=1; //로그인 처리하기
      /*  String gameContentNoString = String.valueOf("game_content_no"); // 문자열로 추출
        int selectedGameContentNo = Integer.parseInt(gameContentNoString); // 문자열을 정수로 변환
        System.out.println("selectedGameContentNo="+selectedGameContentNo);*/

        //System.out.println("selectedGameContentNo=" + game_content_no);
        map.put("member_no", member_no); // map에 member_no 추가
        map.put("game_content_no", game_content_no); // map에 selectedGameContentNo 추가

        List<GameContentsListDTO> gameContents = studyGroupService.selectGame(map);
        //model.addAttribute("gameContents",gameContents);
        System.out.println("엔드포인트gameContents="+gameContents);
        return gameContents;
    }


    //학습 그룹 등록 상세(교육자)
    //학습 그룹 등록 폼
    @GetMapping("/add/{game_content_no}")
    public String add(Model model,StudyGroupForm studyGroupForm
                      ,@PathVariable("game_content_no") int game_content_no,Map<String, Integer> map){

        int member_no=1; //로그인 처리하기

        map.put("member_no", member_no); // map에 member_no 추가
        map.put("game_content_no", game_content_no); // map에 game_content_no 추가
        GameContentsListDTO gameInfo = studyGroupService.getGameInfo(map);
        model.addAttribute("gameInfo",gameInfo);
        System.out.println("gameInfo="+gameInfo);

        return "studyGroup/studyGroup_form";
    }

    //학습 그룹 등록 처리
    @PostMapping("/add/{game_content_no}")
    public String studygroupAdd(Model model, @Valid StudyGroupForm studyGroupForm, BindingResult bindingResult,
                                @PathVariable("game_content_no") int game_content_no,Map<String, Integer> map){
        if(bindingResult.hasErrors()){ //유효성검사시 에러가 발생하면
            //유효성 검사시 게임콘텐츠정보 넘기기
            int member_no=1; //로그인 처리하기

            map.put("member_no", member_no); // map에 member_no 추가
            map.put("game_content_no", game_content_no); // map에 game_content_no 추가
            GameContentsListDTO gameInfo = studyGroupService.getGameInfo(map);
            model.addAttribute("gameInfo",gameInfo);

            return "studyGroup/studyGroup_form"; //studyGroup/studyGroup_form문서로 이동
        }
        int member_no=1; //로그인 처리하기

        GameContents gameContents = gameContentRepository.findById(game_content_no).orElse(null);
        Member member = memberRepository.findById(member_no).orElse(null);

        studyGroupService.add(studyGroupForm.getGroupName(),studyGroupForm.getGroupSize(),studyGroupForm.getGroupStartDate(),studyGroupForm.getGroupFinishDate(),studyGroupForm.getGroupIntroduce(),gameContents,member);

        return "redirect:/studygroup/list";
    }


    //학습 그룹 수정(교육자)
    //학습 그룹 수정 폼
    @GetMapping("/modify/{group_no}")
    public String modify(Model model,StudyGroupForm studyGroupForm,
                         @PathVariable("group_no") int group_no,Map<String, Integer> map){

        StudyGroups studyGroups = studyGroupService.getGruop(group_no);
        studyGroupForm.setGroupName(studyGroups.getGroupName());
        studyGroupForm.setGroupSize(studyGroups.getGroupSize());
        studyGroupForm.setGroupStartDate(studyGroups.getGroupStartDate());
        studyGroupForm.setGroupFinishDate(studyGroups.getGroupFinishDate());
        studyGroupForm.setGroupIntroduce(studyGroups.getGroupIntroduce());

        //studyGroups 엔티티에서 game_content_no,member_no값 추출
        int game_content_no = studyGroups.getGameContents().getGameContentNo();
        int member_no= studyGroups.getMember().getMemberNo();
        map.put("member_no", member_no); // map에 member_no 추가
        map.put("game_content_no", game_content_no); // map에 game_content_no 추가
        GameContentsListDTO gameInfo = studyGroupService.getGameInfo(map);
        model.addAttribute("gameInfo",gameInfo);

        //수정 가능 그룹인원(학습가능인원-(그룹지정된 인원-현재 그룹인원))
        int calculatedValue = gameInfo.getMax_subscribers() - (gameInfo.getAppointed_group_num() - studyGroupForm.getGroupSize());
        model.addAttribute("calculatedValue", calculatedValue);

        return "studygroup/studyGroup_modifyForm";
    }

    //학습 그룹 수정 처리
    @PostMapping("/modify/{group_no}")
    public String studygroupModify(Model model,@Valid StudyGroupForm studyGroupForm,BindingResult bindingResult
                                 ,@PathVariable("group_no") int group_no,Map<String, Integer> map){
        if(bindingResult.hasErrors()){ //유효성검사시 에러가 발생하면
            //유효성 검사시 게임콘텐츠정보 넘기기
            StudyGroups studyGroups = studyGroupService.getGruop(group_no);
            int game_content_no = studyGroups.getGameContents().getGameContentNo();
            int member_no= studyGroups.getMember().getMemberNo();

            map.put("member_no", member_no); // map에 member_no 추가
            map.put("game_content_no", game_content_no); // map에 game_content_no 추가
            GameContentsListDTO gameInfo = studyGroupService.getGameInfo(map);
            model.addAttribute("gameInfo",gameInfo);

            return "studyGroup/studyGroup_modifyForm";
        }
        int member_no=1; //로그인 처리하기

        model.addAttribute("group_no",group_no);
        Member member = memberRepository.findById(member_no).orElse(null);
        StudyGroups studyGroups = studyGroupService.getGruop(group_no);
        studyGroupService.modify(studyGroups,studyGroupForm.getGroupName(),studyGroupForm.getGroupSize(),studyGroupForm.getGroupStartDate(),studyGroupForm.getGroupFinishDate(),studyGroupForm.getGroupIntroduce(),member);
        return "redirect:/studygroup/list";
    }


    //학습 그룹 삭제(교육자)
    @GetMapping("/delete/{group_no}")
    public String delete(@PathVariable("group_no") int group_no){
        StudyGroups studyGroups = studyGroupService.getGruop(group_no);
        studyGroupService.delete(studyGroups);
        return "redirect:/studygroup/list";
    }


    //학습 그룹 목록 조회(교육자)
    @GetMapping(value = "/list")
    public String studygroupList(Model model){
        int member_no=1; //로그인 처리하기

        List<StudyGroupsListDTO> studyGroup = studyGroupService.getList(member_no);
        model.addAttribute("studyGroup",studyGroup);
        System.out.println("studyGroup="+studyGroup);

        //학습그룹명 리스트 가져오기
        List<HashMap<String, Object>> groupNameList = studyGroupService.getGroupName(member_no);
        model.addAttribute("groupNameList",groupNameList);
        System.out.println("groupNameList="+groupNameList);

        return "studyGroup/studyGroup_list";
    }


    //학습 그룹 목록 조회 버튼 엔드포인트
    @GetMapping(value = "/listEndpoint", produces = "application/json")
    @ResponseBody
    public List<StudyGroupsListDTO> studygroupList(@RequestParam Map<String, Integer> map) {
        int member_no=1; //로그인 처리하기

        String groupNoString = String.valueOf(map.get("group_no")); // 문자열로 추출
        int selectedGroupNo = Integer.parseInt(groupNoString); // 문자열을 정수로 변환
        System.out.println("selectedGroupNo="+selectedGroupNo);

        map.put("member_no", member_no); // map에 member_no 추가
        map.put("selectedGroupNo", selectedGroupNo); // map에 selectedGroupNo 추가

        //학습 그룹 정보 가져오기
        List<StudyGroupsListDTO> selectGroup = studyGroupService.selectGroup(map);
        System.out.println("엔드포인트selectGroup="+selectGroup);
        return selectGroup;
    }


    //학습 그룹 상세 조회(교육자)
    @GetMapping("/detail/{group_no}")
    public String groupDetail(Model model, @PathVariable("group_no") int group_no){
        //학습 그룹 멤버 정보
        List<GroupsDetailListDTO> GroupDetail = studyGroupService.getDetailList(group_no);
        //학습 그룹 정보
        List<GroupsDetailListDTO> GroupInfo = studyGroupService.getGroupInfo(group_no);
        model.addAttribute("GroupDetail",GroupDetail);
        model.addAttribute("GroupInfo",GroupInfo);
        System.out.println("GroupDetail="+GroupDetail);
        System.out.println("GroupInfo="+GroupInfo);
        model.addAttribute("group_no",group_no);

        //학습 그룹 이름 가져오기(SelectBox)
        int member_no=1; //로그인 처리하기

        List<HashMap<String, Object>> groupNameList = studyGroupService.getGroupName(member_no);
        model.addAttribute("groupNameList",groupNameList);

        return "studyGroup/studyGroup_detail";
    }


    //학습 그룹 가입 승인(교육자)
    //학습 그룹 가입 신청 리스트 가져오기
    @GetMapping("/approveList")
    public String approveList(Model model, @RequestParam Map<String, Integer> map){
        int member_no=1; //로그인 처리하기

        //학습그룹명 리스트 가져오기
        List<HashMap<String, Object>> groupNameList = studyGroupService.getGroupName(member_no);
        model.addAttribute("groupNameList",groupNameList);
        System.out.println("groupNameList="+groupNameList);

        //학습 그룹 신청 리스트에서 기본으로 보여질 group_no(학습그룹no 중 가장 작은 값)
        int selectedGroupNo = studyGroupService.basicGroupNo(member_no);

        //학습 그룹 정보
        List<GroupsDetailListDTO> GroupInfo = studyGroupService.getGroupInfo(selectedGroupNo);
        model.addAttribute("GroupInfo",GroupInfo);

        map.put("member_no", member_no); // map에 member_no 추가
        map.put("group_no", selectedGroupNo); // map에 selectedGroupNo 추가

        //학습 그룹 가입 신청 학생 리스트
        List<ApproveListDTO> approveList = studyGroupService.getApproveList(map);
        model.addAttribute("approveList",approveList);
        System.out.println("approveList="+approveList);
        return "studyGroup/studyGroup_approveList";
    }


    //학습 그룹 가입 신청 리스트 엔드포인트
    @GetMapping(value = "/approveListEndpoint", produces = "application/json")
    @ResponseBody
    public ResponseEntity<GroupInfoAndApproveList> approveListEndPoint(@RequestParam Map<String, Integer> map) {
        int member_no=1; //로그인 처리하기

        String groupNoString = String.valueOf(map.get("group_no"));
        int selectedGroupNo = Integer.parseInt(groupNoString);
        System.out.println("selectedGroupNo=" + selectedGroupNo);

        //학습 그룹 정보
        List<GroupsDetailListDTO> GroupInfo = studyGroupService.getGroupInfo(selectedGroupNo);
        System.out.println("GroupInfo=" + GroupInfo);

        map.put("member_no", member_no);
        map.put("selectedGroupNo", selectedGroupNo);

        //학습 그룹 가입 신청 학생 리스트
        List<ApproveListDTO> approveList = studyGroupService.getApproveList(map);
        System.out.println("approveList=" + approveList);

        GroupInfoAndApproveList result = new GroupInfoAndApproveList(GroupInfo, approveList);
        //GroupInfo,approveList 결과 보내기
        return new ResponseEntity<>(result, HttpStatus.OK);
    }



    //학습 그룹 가입 승인 처리(교육자)
    @PostMapping("/updateApprove")
    public String approve(@RequestBody Map<String, List<Integer>> requestData) {
        List<Integer> selectedMembers = requestData.get("selectedMembers");
        System.out.println("selectedMembers="+selectedMembers);

        if (selectedMembers == null || selectedMembers.isEmpty()) {
            return "redirect:/studygroup/approveList";
        }

        int result = studyGroupService.updateApproval(selectedMembers);
        System.out.println("result="+result);
        if (result == 0) { // 수정 실패시
            return "redirect:/studygroup/approveList";
        } else {
            return "redirect:/studygroup/approveList";
        }
    }

/*    @PostMapping(value = "/updateApproveEnd", produces = "application/json")
    public ResponseEntity<String> updateApproveEnd(@RequestBody List<Integer> selectedMembers) {
        System.out.println("updateApproveEnd진입");
        // 'selectedMembers' 데이터를 사용하여 필요한 작업을 수행 (예: 데이터베이스 업데이트)
        int result = studyGroupService.updateApproval(selectedMembers);

        if (result > 0) {
            return ResponseEntity.ok("Data processed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Data processing failed");
        }
    }*/


    //학습 그룹 가입 신청(학생),학습 그룹 리스트
    @GetMapping("/groupJoinList")
    public String joinList(Model model){
        //학습그룹명 리스트(전체) 가져오기
        List<HashMap<String, Object>> groupNameALL = studyGroupService.getGroupNameALL();
        model.addAttribute("groupNameALL",groupNameALL);
        //교육자명 리스트(전체) 가져오기
        List<HashMap<String, Object>> educatorName = studyGroupService.getEducatorName();
        model.addAttribute("educatorName",educatorName);

        List<GroupJoinListDTO> groupJoinList = studyGroupService.getGroupJoinList();
        model.addAttribute("groupJoinList",groupJoinList);
        System.out.println("groupJoinList="+groupJoinList);
        return "studyGroup/studyGroup_joinList";
    }

    //학습 그룹 가입 신청(학생),학습 그룹 리스트 엔드포인트
    @GetMapping(value = "/groupJoinListEndpoint", produces = "application/json")
    @ResponseBody
    public List<GroupJoinListDTO> groupJoinListEndpoint(@RequestParam(required = false) Integer group_no, @RequestParam(required = false) Integer member_no){
        if (group_no != null && member_no != null) {
            // 두 파라미터가 동시에 전달될 경우 처리
            System.out.println("같이");
            System.out.println("group_no=" + group_no);
            System.out.println("member_no=" + member_no);

            Map<String, Integer> map = new HashMap<>();
            map.put("group_no", group_no);
            map.put("member_no", member_no);

            List<GroupJoinListDTO> groupJoinList = studyGroupService.SelectGroupJoinList(map);
            return groupJoinList;
        } else if (group_no != null) {
            // group_no를 이용한 처리
            List<GroupJoinListDTO> groupJoinListByGroup = studyGroupService.selectNameList(group_no);
            System.out.println("group_no=" + group_no);
            return groupJoinListByGroup;
        } else if (member_no != null) {
            // member_no를 이용한 처리
            List<GroupJoinListDTO> groupJoinListByMember = studyGroupService.SelectEducatorNameList(member_no);
            System.out.println("member_no=" + member_no);
            return groupJoinListByMember;
        } else {
            // 두 파라미터 중 하나도 전달되지 않은 경우 처리
            return Collections.emptyList();
        }
    }



    //학습 그룹 가입 신청 처리(학생)
    @PostMapping("/join/{group_no}")
    public String join( GroupJoinDTO groupJoinDTO,@PathVariable("group_no") int group_no){
        int member_no=3; //로그인 처리하기

        Member member = memberRepository.findById(member_no).orElse(null);

        StudyGroups studyGroups = groupRepository.findById(group_no).orElse(null);
        studyGroupService.groupJoin(studyGroups,member,groupJoinDTO.getApplication_date(),groupJoinDTO.getIs_approved(),groupJoinDTO.getApproved_date());

        //학습 그룹 신청 했으면 중복 신청 불가능->학습 그룸 가입 확인으로 이동


        return "redirect:/studygroup/groupJoinList";
    }


    //학습 그룹 가입 확인(학생)
    @GetMapping("/joinConfirm")
    public String joinConfirm(Model model){
        int member_no=16; //로그인 처리하기

        JoinConfirmDTO joinConfirm = studyGroupService.joinConfirm(member_no);
        model.addAttribute("joinConfirm",joinConfirm);
        System.out.println("joinConfirm="+joinConfirm);

        if(joinConfirm==null){
            //alert창 띄우기
            MessageDto message = new MessageDto("가입 신청된 학습 그룹이 없습니다.", "/studygroup/groupJoinList", RequestMethod.GET, null);
            return showMessageAndRedirect(message,model);
        }return "studyGroup/studyGroup_joinConfirm";
    }


    //학습 그룹 가입 취소(학생)
    @GetMapping("/cancel/{group_students_no}")
    public String cancel(@PathVariable("group_students_no") int group_students_no){
        GroupStudents groupStudents = studyGroupService.getGroupStudents(group_students_no);
        studyGroupService.cancel(groupStudents);
        return "redirect:/studygroup/groupJoinList";
    }


    // alert창(사용자에게 메시지를 전달하고, 페이지를 리다이렉트 함)
    private String showMessageAndRedirect(final MessageDto params, Model model) {
        model.addAttribute("params", params);
        return "studygroup/messageRedirect";
    }

}
