package com.idukbaduk.metoo9dan.member.validation;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

//유효성 검사용 클래스
@Getter
@Setter
public class UserCreateForm {

    private Integer memberNo;

    @Size(min = 2, max = 5)
    @NotEmpty(message = "이름은 필수입력입니다.")
    private String name;          //'회원 이름'

    @NotBlank(message = "핸드폰번호는 필수 항목입니다.")
    @Size(max = 20, message = "핸드폰번호는 20자 이하여야 합니다.")
    private String tel;     //회원 휴대폰번호

    @NotEmpty(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;        //'회원 이메일'

    @NotEmpty(message = "아이디는 필수입력입니다.")
    @Size(max = 20, message = "아이디는 20자 이하여야 합니다.")
    private String memberId;            //'회원 아이디'

    @NotNull(message = "생년월일은 필수입력입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birth;

    @NotEmpty(message = "성별을 선택하세요.")
    private String gender;             //'성별'

    @NotBlank(message = "회원구분을 선택해주세요.")
    private String role;   //enum('admin', 'educator', 'student', 'pendingapproval')    NOT NULL    COMMENT '회원구분',

    @NotNull(message = "개인정보 수집 동의를 하셔야 회원 가입이 가능합니다.")
    private Boolean privacyConsent;  //boolean NOT NULL    COMMENT '개인정보동의',

    private Boolean emailConsent;    //boolean NOT NULL    COMMENT '이메일수신동의',

    private Boolean smsConsent;      //boolean NOT NULL    COMMENT 'sms수신동의',

    private String memberMemo;      //varchar(300) NULL COMMENT "회원별 메모"

    private LocalDateTime joinDate;    //'가입일'

    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{6,}$",
            message = "비밀번호는 최소 6자리 이상 숫자, 문자, 특수문자 각각 1개 이상 포함 되어야 합니다.")
    private String pwd1; //'비밀번호'

    @NotEmpty(message = "비밀번호확인은 필수입력입니다.")
    private String pwd2;                //'비밀번호'

    // EducatorInfo 정보
    @NotEmpty(message = "소속학원이 위치한 '시도'를 입력해주세요.")
    private String sido;         //varchar(20)    NOT NULL    COMMENT '시도',

    @NotEmpty(message = "소속학원이 위치한 '시군구'를 입력해주세요.")
    private String sigungu;      //varchar(20)    NOT NULL    COMMENT '시군구',

    @NotEmpty(message = "소속 학원명을 입력해주세요.")
    private String schoolName;   //varchar(20)    NOT NULL    COMMENT '소속명',

}
