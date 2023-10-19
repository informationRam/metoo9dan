package com.idukbaduk.metoo9dan.education.vaildation;

import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class EducationValidation {

    private Integer resource_no;

    @NotEmpty(message = "교육자료명을 입력해주세요.")
    private String resource_name;    //'교육자료명',

    @NotEmpty(message = "자료구분을 선택해주세요.")
    private String resource_cate;    //'자료 구분',

    private String file_type;        //'자료 유형',

    @NotEmpty(message = "서비스 구분을 선택해주세요.")
    private String service_type;     //enum('paid', 'free') '서비스 구분',

    private String description;      //'자료내용'

    private MultipartFile boardFile;  //  교육자료파일저장시 필요

    private MultipartFile thumFile;  //  썸네일파일저장시 필요

    private ResourcesFiles saveboardFile; // 이미 업로드된 이미지 파일 목록

    private List<Integer> deletedFiles;   // 삭제된 파일 목록

    private ResourcesFiles saveThumFile;  //이미 업로드된 이미지 파일 목록

    private String origin_file_name;  //'원본파일명'

}
