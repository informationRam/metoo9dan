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
    private String gameTitle; //숙제 받은놈 이름
    private Integer currentLevel;
    private LocalDateTime sendDate;
    private Integer homeworkNo;
    private String name; //숙제 받은놈 이름

    private Integer homeworkSubmitNo;
    private String submitContent;
    private String additionalQuestions;
    private LocalDateTime submitDate;
    private String evaluation;

    public HwSendSubmitDTO(HomeworkSend homeworkSend) {
        this.homeworkTitle = homeworkSend.getHomeworks().getHomeworkTitle();
        this.homeworkContent = homeworkSend.getHomeworks().getHomeworkContent();
        this.gameTitle = homeworkSend.getHomeworks().getGameTitle();
        this.dueDate = homeworkSend.getHomeworks().getDueDate();
        this.sendNo = homeworkSend.getSendNo();
        this.currentLevel = homeworkSend.getCurrentLevel();
        this.sendDate = homeworkSend.getSendDate();
        this.homeworkNo = homeworkSend.getHomeworks().getHomeworkNo();
        this.name = homeworkSend.getMember().getName();

        this.homeworkSubmitNo = 0;
        this.submitContent = "";
        this.additionalQuestions = "";
        this.submitDate = LocalDateTime.now();
        this.evaluation = "C";
    }
    public HwSendSubmitDTO(HomeworkSend homeworkSend, HomeworkSubmit homeworkSubmit) {
        this.homeworkTitle = homeworkSend.getHomeworks().getHomeworkTitle();
        this.homeworkContent = homeworkSend.getHomeworks().getHomeworkContent();
        this.dueDate = homeworkSend.getHomeworks().getDueDate();
        this.sendNo = homeworkSend.getSendNo();
        this.gameTitle = homeworkSend.getHomeworks().getGameTitle();
        this.currentLevel = homeworkSend.getCurrentLevel();
        this.sendDate = homeworkSend.getSendDate();
        this.homeworkNo = homeworkSend.getHomeworks().getHomeworkNo();
        this.name = homeworkSend.getMember().getName();

        this.homeworkSubmitNo = homeworkSubmit.getHomeworkSubmitNo();
        this.submitContent = homeworkSubmit.getHomeworkContent();
        this.additionalQuestions = homeworkSubmit.getAdditionalQuestions();
        this.submitDate = homeworkSubmit.getSubmitDate();
        this.evaluation = homeworkSubmit.getEvaluation();
    }

}
