package com.idukbaduk.metoo9dan.homework.domain;

import com.idukbaduk.metoo9dan.common.entity.HomeworkSend;
import com.idukbaduk.metoo9dan.common.entity.Homeworks;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.StudyGroups;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
@RequiredArgsConstructor
@ToString
public class GroupStudentDTO {

    private Integer groupStudentsNo;
    private LocalDateTime applicationDate;
    private Boolean isApproved;
    private Integer groupNo;
    private Integer memberNo;
    private String name;
    private String tel;
    private Integer currentLevel;

}

