package com.idukbaduk.metoo9dan.homework.service;

import com.idukbaduk.metoo9dan.common.entity.*;
import com.idukbaduk.metoo9dan.homework.validation.HwSubmitForm;
import com.idukbaduk.metoo9dan.homework.domain.GroupStudentDTO;
import com.idukbaduk.metoo9dan.homework.domain.HomeworkDTO;
import com.idukbaduk.metoo9dan.homework.domain.HomeworkSubmitDetailDTO;
import com.idukbaduk.metoo9dan.homework.domain.HomeworkSubmitDTO;
import com.idukbaduk.metoo9dan.homework.repository.*;
import com.idukbaduk.metoo9dan.homework.validation.HomeworksEditForm;
import com.idukbaduk.metoo9dan.homework.validation.HomeworksForm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HomeworkService {

    @Autowired
    private HomeworkRepository homeworkRepository;
    @Autowired
    private HomeworkSendRepository homeworkSendRepository;

    @Autowired
    private HwMemberRepository memberRepository;

    @Autowired
    private GroupStudentsRepository groupStudentsRepository;
    @Autowired
    private StudyGroupsRepository studyGroupsRepository;

    @Autowired
    private HomeworkSubmitRepository homeworkSubmitRepository;

    public List<GroupStudentDTO> getGroupStudentsWithLatestProgress(Integer groupNo) {
        List<GroupStudents> groupStudents = groupStudentsRepository.findByIsApprovedAndStudyGroupsGroupNo(true,groupNo);
        List<GroupStudentDTO> result = new ArrayList<>();

        for (GroupStudents student : groupStudents) {
            GroupStudentDTO dto = new GroupStudentDTO();
            dto.setGroupStudentsNo(student.getGroupStudentsNo());
            dto.setApplicationDate(student.getApplicationDate());
            dto.setIsApproved(student.getIsApproved());
            dto.setGroupNo(student.getStudyGroups().getGroupNo());
            dto.setMemberNo(student.getMember().getMemberNo());
            dto.setName(student.getMember().getName());
            dto.setTel(student.getMember().getTel());

            // Get the latest HomeworkSend and set the currentLevel to the DTO
            List<HomeworkSend> latestHomeworks = homeworkSendRepository.findLatestHomeworkSendByMemberNo(student.getMember().getMemberNo());
            if (!latestHomeworks.isEmpty()) {
                dto.setCurrentLevel(latestHomeworks.get(0).getCurrentLevel());
            } else {
                dto.setCurrentLevel(0);
            }
            result.add(dto);
        }

        return result;
    }
    public List<StudyGroups> findStudyGroupsByMemberId(String memberId){
        return studyGroupsRepository.findStudyGroupsByMemberId(memberId);
    }
    public Member findMemberByMemberId(String memberId){
        return memberRepository.findByMemberId(memberId);
    }
    public List<Homeworks> findHomeworksByMemberIdAndDueDateAfter(String name){
        return homeworkRepository.findHomeworksByMemberIdAndDueDateAfter(name,new Date());
    }
    public Homeworks findById(Integer homeworkId){
        Optional<Homeworks> optionalHomeworks =homeworkRepository.findById(homeworkId);
        return optionalHomeworks.orElse(null);
    }
    public void saveHomework(@Valid HomeworksForm homework, Member member) {
        Homeworks hw = new Homeworks();
        hw.setHomeworkTitle(homework.getHomeworkTitle());
        hw.setHomeworkContent(homework.getHomeworkContent());
        hw.setHomeworkMemo(homework.getHomeworkMemo());
        hw.setMember(member);
        hw.setDueDate(homework.getDueDate());
        hw.setProgress(homework.getProgress());
        hw.setCreationDate(LocalDateTime.now()); //현재시간으로 설정
        homeworkRepository.save(hw);
    }

    public void updateHomework(@Valid HomeworksEditForm homeworksEditForm, Homeworks homeworks) {
        homeworks.setHomeworkTitle(homeworksEditForm.getHwTitle());
        homeworks.setHomeworkContent(homeworksEditForm.getHwContent());
        homeworks.setHomeworkMemo(homeworksEditForm.getHwMemo());
        homeworks.setDueDate(homeworksEditForm.getHwDueDate());
        homeworks.setProgress(homeworksEditForm.getHwProgress());
        //homeworks.getCreationDate(new LocalDateTime.now());

        homeworkRepository.save(homeworks);
    }
    public List<HomeworkDTO> findAllHomeworkWithSendStatus(String memberId) {
        List<Homeworks> homeworks = homeworkRepository.findByMember_MemberIdOrderByCreationDateDesc(memberId);
        // Convert homeworks list into HomeworkDTO list and check if it has been sent
        return homeworks.stream().map(hw -> {
            HomeworkDTO dto = new HomeworkDTO(hw);
            List<HomeworkSend> sentHomeworks = homeworkSendRepository.findByHomeworks(hw);
            dto.setSent(!sentHomeworks.isEmpty());
            return dto;
        }).collect(Collectors.toList());
    }
    public List<HomeworkDTO> toHomeworkDTOList(List<Homeworks> homeworks){
        return homeworks.stream().map(hw -> {
            HomeworkDTO dto = new HomeworkDTO(hw);
            List<HomeworkSend> sentHomeworks = homeworkSendRepository.findByHomeworks(hw);
            dto.setSent(!sentHomeworks.isEmpty());
            return dto;
        }).collect(Collectors.toList());
    }
    public Optional<Homeworks> getHomeworkById(Integer id) {
        return homeworkRepository.findById(id);
    }

    public void deleteHomework(Integer id) {
        homeworkRepository.deleteById(id);
    }

    public List<HomeworkSend> getHomeworkSendListByhomework(Integer homeworkId) {
        return homeworkSendRepository.findByHomeworks_HomeworkNo(homeworkId);
    }

    public List<String> saveHomeworksForMembers(List<String> homeworks, List<String> members) {
        List<String> skippedEntries = new ArrayList<>(); // 건너뛴 학생과 숙제의 조합을 저장하기 위한 리스트

        for (String homework : homeworks) {
            for (String member : members) {
                Optional<Homeworks> hw = homeworkRepository.findById(Integer.parseInt(homework));
                Optional<Member> mem = memberRepository.findById(Integer.parseInt(member));

                if (hw.isPresent() && mem.isPresent()) {
                    // 이미 존재하는 조합인지 확인
                    boolean exists = homeworkSendRepository.existsByHomeworksAndMember(hw.get(), mem.get());

                    if (!exists) {
                        HomeworkSend homeworkSend = new HomeworkSend();
                        homeworkSend.setHomeworks(hw.get());
                        homeworkSend.setMember(mem.get());
                        homeworkSend.setCurrentLevel(hw.get().getProgress());
                        homeworkSend.setSendDate(LocalDateTime.now());
                        homeworkSend.setIsSubmit("N");
                        homeworkSendRepository.save(homeworkSend);
                    } else {
                        skippedEntries.add("Member ID: " + member + " - Homework ID: " + homework); // 건너뛴 조합 저장
                    }
                }
            }
        }
        return skippedEntries; // 건너뛴 학생과 숙제의 조합 리스트 반환
    }

    public List<HomeworkSend> findHomeworkSendByMemberId(String memberId) {
        //제출기한 안끝난 숙제만 가져오기
        return homeworkSendRepository.findByMemberIdAndDueDateAfterCurrentDate(memberId);

    }

    public HomeworkSubmitDetailDTO getDetail(Integer sendNo) {
        /*
    -private Integer homeworkNo;
    -private Integer sendNo;
    -private Integer homeworkSubmitNo;
    -private String homeworkTitle;
    -private String homeworkContent;
    -private String memberName;//교육자명
    -private Integer progress;
    -private Date dueDate;
    -private String content;
    -private String additionalQuestion;
         */
        HomeworkSubmitDetailDTO dto = new HomeworkSubmitDetailDTO();
        dto.setSendNo(sendNo);

        Optional<HomeworkSend> optionalHomeworkSend =homeworkSendRepository.findById(sendNo);
        if(optionalHomeworkSend.isPresent()){
            Homeworks homeworks = optionalHomeworkSend.get().getHomeworks();
            dto.setHomeworkNo(homeworks.getHomeworkNo());
            dto.setHomeworkTitle(homeworks.getHomeworkTitle());
            dto.setHomeworkContent(homeworks.getHomeworkContent());
            dto.setMemberName(homeworks.getMember().getName());
            dto.setProgress(homeworks.getProgress());
            dto.setDueDate(homeworks.getDueDate());
            dto.setSendNo(optionalHomeworkSend.get().getSendNo());
        }
        Optional<HomeworkSubmit> optionalHomeworkSubmit = homeworkSubmitRepository.findByHomeworkSend_SendNo(sendNo);
        if(optionalHomeworkSubmit.isPresent()){
            HomeworkSubmit homeworkSubmit =optionalHomeworkSubmit.get();
            System.out.println("homeworkSubmit.getHomeworkSubmitNo():"+homeworkSubmit.getHomeworkSubmitNo());
            dto.setHomeworkSubmitNo(homeworkSubmit.getHomeworkSubmitNo());
            dto.setContent(homeworkSubmit.getHomeworkContent());
            dto.setAdditionalQuestion(homeworkSubmit.getAdditionalQuestions());
        }
        System.out.println("dto:"+dto);
        return dto;
    }

    public Optional<HomeworkSend> getHomeworkSendById(Integer homeworkSendNo) {
        return homeworkSendRepository.findById(homeworkSendNo);
    }

    public void addHomeworkSubmit(@Valid HwSubmitForm hwSubmitForm, Homeworks homeworks, HomeworkSend homeworkSend) {
        homeworkSend.setIsSubmit("Y");
        homeworkSendRepository.save(homeworkSend);

        HomeworkSubmit homeworkSubmit = new HomeworkSubmit();
        homeworkSubmit.setHomeworkSend(homeworkSend);
        homeworkSubmit.setHomeworks(homeworks);
        homeworkSubmit.setHomeworkContent(hwSubmitForm.getHomeworkContent());
        homeworkSubmit.setMember(hwSubmitForm.getMember());
        homeworkSubmit.setSubmitDate(LocalDateTime.now());
        homeworkSubmit.setAdditionalQuestions(hwSubmitForm.getAdditionalQuestions());
        homeworkSubmit.setProgress(homeworks.getProgress());
        homeworkSubmitRepository.save(homeworkSubmit);
    }

    public HomeworkSubmit getHomeworkSubmitByHomeworkSubmitNo(Integer submitNo){
        Optional<HomeworkSubmit> optionalHomeworkSubmit = homeworkSubmitRepository.findById(submitNo);
        return optionalHomeworkSubmit.orElse(null);
    }

    public void updateHomeworkSubmitted(@Valid HwSubmitForm hwSubmitForm, HomeworkSubmit homeworkSubmit) {
        homeworkSubmit.setHomeworkContent(hwSubmitForm.getHomeworkContent());
        homeworkSubmit.setAdditionalQuestions(hwSubmitForm.getAdditionalQuestions());
        homeworkSubmit.setSubmitDate(LocalDateTime.now());

        homeworkSubmitRepository.save(homeworkSubmit);
    }

    public void deleteHomeworkSubmitted(Integer homeworkSubmitNo){
        Optional<HomeworkSubmit> optionalHomeworkSubmit =homeworkSubmitRepository.findById(homeworkSubmitNo);
        if(optionalHomeworkSubmit.isPresent()){
            HomeworkSend homeworkSend =optionalHomeworkSubmit.get().getHomeworkSend();
            homeworkSend.setIsSubmit("N");
            homeworkSendRepository.save(homeworkSend);
        }else{
            System.out.println("N으로 바꾸는거 실패");
        }

        homeworkSubmitRepository.deleteById(homeworkSubmitNo);
    }

    /*
    public Page<HomeworkSend> fetchData(String title, Pageable pageable) {
        // 간단한 예시: 이름으로 검색
        return homeworkSendRepository.findByNameContaining(query, pageable);
    }

    public List<String> fetchAllTitles() {
        return homeworkSendRepository.findAllTitles();
    }
     */
}
