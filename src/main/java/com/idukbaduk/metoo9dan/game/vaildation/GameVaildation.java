package com.idukbaduk.metoo9dan.game.vaildation;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.sql.Insert;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GameVaildation {


    private Insert game_no;

    @NotBlank(message = "게임콘텐츠명은 필수 입니다.")
    private String game_name;             //'게임 콘텐츠명'

    @NotEmpty(message = "난이도를 선택해주세요.")
    private String difficulty;             //'난이도'

    @Min(value = 1, message = "최소값은 1 이상이어야 합니다.")
    @NotNull(message = "구독기간은 필수 입니다.")
    private Integer subscription_duration;  //'구독기간'

    @Min(value = 1, message = "최소값은 1 이상이어야 합니다.")
    @NotNull(message = "구독인원은 필수 입니다.")
    private Integer max_subscribers;        //'구독 인원'

    @NotNull(message = "정가입력은 필수 입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "0 이상의 숫자여야 합니다.")
    private Double original_price;         //'정가'

    @NotNull(message = "할인율 입력은 필수 입니다.")
    @DecimalMax(value = "100.0", inclusive = true, message = "0부터 100까지의 숫자여야 합니다.")
    private Double discount_rate;          //'할인율'

    @DecimalMin(value = "0.0", inclusive = false, message = "0 이상의 숫자여야 합니다.")
    private Double sale_price;             //'판매가'

    private String package_details;        //'패키지 내용'

    private LocalDateTime creation_date;  // '등록일'

    private String status;                //'게시글 상태'

    private String content_type;

    private List<MultipartFile> boardFile;  //  파일저장시 필요

    private List<MultipartFile> boardFileList;  // 이미 업로드된 이미지 파일 목록

    private String origin_file_name;  //'원본파일명'

    private int[] selectedResourceNos;  //선택된 ResourceNo 값들
}
