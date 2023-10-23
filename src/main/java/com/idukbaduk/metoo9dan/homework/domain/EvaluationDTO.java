package com.idukbaduk.metoo9dan.homework.domain;

import com.idukbaduk.metoo9dan.common.entity.HomeworkSend;
import com.idukbaduk.metoo9dan.common.entity.HomeworkSubmit;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Data
@RequiredArgsConstructor
@ToString
public class EvaluationDTO {
    private String homeworkSendNo;
    private String evaluationValue;
}
