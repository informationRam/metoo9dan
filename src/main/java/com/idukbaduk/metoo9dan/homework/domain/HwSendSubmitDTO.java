package com.idukbaduk.metoo9dan.homework.domain;

import com.idukbaduk.metoo9dan.common.entity.HomeworkSend;
import com.idukbaduk.metoo9dan.common.entity.HomeworkSubmit;
import com.idukbaduk.metoo9dan.common.entity.Homeworks;
import com.idukbaduk.metoo9dan.common.entity.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Data
@RequiredArgsConstructor
@ToString
public class HwSendSubmitDTO {
    private String homeworkTitle;
    private String homeworkContent;
    private Date dueDate;

    private Integer sendNo;
    private Integer currentLevel;
    private LocalDateTime sendDate;
    private Integer homeworkNo;
    private String name; //숙제 받은놈 이름

    private Integer homeworkSubmitNo;
    private String submitContentPreview;
    private String submitContent;
    private String additionalQuestionsPreview;
    private String additionalQuestions;
    private LocalDateTime submitDate;
    private String evaluation;

    public HwSendSubmitDTO(HomeworkSend homeworkSend) {
        this.homeworkTitle = homeworkSend.getHomeworks().getHomeworkTitle();
        this.homeworkContent = homeworkSend.getHomeworks().getHomeworkContent();
        this.dueDate = homeworkSend.getHomeworks().getDueDate();
        this.sendNo = homeworkSend.getSendNo();
        this.currentLevel = homeworkSend.getCurrentLevel();
        this.sendDate = homeworkSend.getSendDate();
        this.homeworkNo = homeworkSend.getHomeworks().getHomeworkNo();
        this.name = homeworkSend.getMember().getName();
    }
    public HwSendSubmitDTO(HomeworkSend homeworkSend, HomeworkSubmit homeworkSubmit) {
        this.homeworkTitle = homeworkSend.getHomeworks().getHomeworkTitle();
        this.homeworkContent = homeworkSend.getHomeworks().getHomeworkContent();
        this.dueDate = homeworkSend.getHomeworks().getDueDate();
        this.sendNo = homeworkSend.getSendNo();
        this.currentLevel = homeworkSend.getCurrentLevel();
        this.sendDate = homeworkSend.getSendDate();
        this.homeworkNo = homeworkSend.getHomeworks().getHomeworkNo();
        this.name = homeworkSend.getMember().getName();

        this.homeworkSubmitNo = homeworkSubmit.getHomeworkSubmitNo();
        this.submitContentPreview = homeworkSubmit.getHomeworkContent().length() > 10 ?
                homeworkSubmit.getHomeworkContent().substring(0, 10) + "..." :
                homeworkSubmit.getHomeworkContent();
        this.submitContent = homeworkSubmit.getHomeworkContent();
        this.additionalQuestionsPreview = homeworkSubmit.getAdditionalQuestions().length() > 10 ?
                homeworkSubmit.getAdditionalQuestions().substring(0, 10) + "..." :
                homeworkSubmit.getAdditionalQuestions();
        this.additionalQuestions = homeworkSubmit.getAdditionalQuestions();
        this.submitDate = homeworkSubmit.getSubmitDate();
        this.evaluation = homeworkSubmit.getEvaluation();
    }

}
