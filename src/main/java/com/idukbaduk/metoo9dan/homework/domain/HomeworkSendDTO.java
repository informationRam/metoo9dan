package com.idukbaduk.metoo9dan.homework.domain;

import com.idukbaduk.metoo9dan.common.entity.HomeworkSend;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
@Getter
@Setter
@Data
@RequiredArgsConstructor
@ToString
public class HomeworkSendDTO {
    private Integer homeworkNo;
    private String homeworkTitle;
    private String homeworkContentPreview;
    private String homeworkContent;
    private Date dueDate;
    private Integer currentLevel;
    private LocalDateTime sendDate;

    public HomeworkSendDTO(HomeworkSend homeworkSend) {
        this.homeworkNo = homeworkSend.getHomeworks().getHomeworkNo();
        this.homeworkTitle = homeworkSend.getHomeworks().getHomeworkTitle();
        this.homeworkContentPreview = homeworkSend.getHomeworks().getHomeworkContent().length() > 10 ?
                homeworkSend.getHomeworks().getHomeworkContent().substring(0, 10) + "..." :
                homeworkSend.getHomeworks().getHomeworkContent();
        this.homeworkContent=homeworkSend.getHomeworks().getHomeworkContent();
        this.dueDate=homeworkSend.getHomeworks().getDueDate();
        this.currentLevel = homeworkSend.getCurrentLevel();
        this.sendDate = homeworkSend.getSendDate();
    }
}
