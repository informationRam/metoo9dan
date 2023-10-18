package com.idukbaduk.metoo9dan.homework.domain;

import com.idukbaduk.metoo9dan.common.entity.HomeworkSend;
import com.idukbaduk.metoo9dan.common.entity.Homeworks;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
@RequiredArgsConstructor
@ToString
public class HomeworkDTO {

    private Integer homeworkNo;
    private String homeworkTitle;
    private String homeworkContentPreview;
    private String homeworkContent;
    private String homeworkMemo;
    private String gameTitle;
    private Integer progress;
    private Date dueDate;
    private LocalDateTime creationDate;
    private boolean isSent;
    private List<HomeworkSend> homeworkSendList;

    // 추가된 생성자
    public HomeworkDTO(Homeworks homeworks) {
        this.homeworkNo = homeworks.getHomeworkNo();
        this.homeworkTitle = homeworks.getHomeworkTitle();
        this.homeworkContentPreview = homeworks.getHomeworkContent().length() > 10 ?
                homeworks.getHomeworkContent().substring(0, 10) + "..." :
                homeworks.getHomeworkContent();
        this.homeworkContent=homeworks.getHomeworkContent();
        this.homeworkMemo=homeworks.getHomeworkMemo();
        this.progress = homeworks.getProgress();
        this.gameTitle = homeworks.getGameTitle();
        this.dueDate = homeworks.getDueDate();
        this.creationDate=homeworks.getCreationDate();
    }
}

