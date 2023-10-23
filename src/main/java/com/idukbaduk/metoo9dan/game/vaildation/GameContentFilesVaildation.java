package com.idukbaduk.metoo9dan.game.vaildation;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GameContentFilesVaildation {

    private String origin_filename;  //varchar(100)    NOT NULL    COMMENT '원본파일명',

    private String copyFileName;    //varchar(100)    NOT NULL    COMMENT '사본파일명',

}
