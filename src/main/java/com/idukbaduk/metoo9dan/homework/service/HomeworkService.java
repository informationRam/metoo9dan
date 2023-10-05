package com.idukbaduk.metoo9dan.homework.service;

import com.idukbaduk.metoo9dan.common.entity.*;
import com.idukbaduk.metoo9dan.homework.domain.GroupStudentDTO;
import com.idukbaduk.metoo9dan.homework.domain.HomeworkDTO;
import com.idukbaduk.metoo9dan.homework.repository.*;
import com.idukbaduk.metoo9dan.homework.validation.HomeworksForm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

@Service
public class HomeworkService {

    @Autowired
    private HomeworkRepository homeworkRepository;
    @Autowired
    private HomeworkSendRepository homeworkSendRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GroupStudentsRepository groupStudentsRepository;
    @Autowired
    private StudyGroupsRepository studyGroupsRepository;

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
    public List<HomeworkDTO> findAllHomeworkWithSendStatus(String memberId) {
        List<Homeworks> homeworks = homeworkRepository.findByMember_MemberIdOrderByCreationDateDesc(memberId);
        // Convert homeworks list into HomeworkDTO list and check if it has been sent
        return homeworks.stream().map(hw -> {
            HomeworkDTO dto = new HomeworkDTO(hw);
            dto.setSent(homeworkSendRepository.findByHomeworks(hw).isPresent());
            return dto;
        }).collect(Collectors.toList());
    }
    public List<HomeworkDTO> toHomeworkDTOList(List<Homeworks> homeworks){
        return homeworks.stream().map(hw -> {
            HomeworkDTO dto = new HomeworkDTO(hw);
            dto.setSent(homeworkSendRepository.findByHomeworks(hw).isPresent());
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
}
