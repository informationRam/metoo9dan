package com.idukbaduk.metoo9dan.education.vaildation;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class EducationVaildation {

    @NotEmpty(message = "교육자료명을 입력해주세요.")
    private String resource_name;    //'교육자료명',

    @NotEmpty(message = "자료구분을 선택해주세요.")
    private String resource_cate;    //'자료 구분',

    private String file_type;        //'자료 유형',

    private String file_url;         //'자료url',

    @NotEmpty(message = "서비스 구분을 선택해주세요.")
    private String service_type;     //enum('paid', 'free') '서비스 구분',

    private String description;      //'자료내용'

    private MultipartFile boardFile;

    private String origin_file_name;  //'원본파일명'

}
