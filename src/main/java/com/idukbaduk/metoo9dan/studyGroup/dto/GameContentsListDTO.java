package com.idukbaduk.metoo9dan.studyGroup.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class GameContentsListDTO {
    private int game_content_no;
    private String game_name;//게임콘텐츠명
    //private int subscription_duration; //구독기간
    private Date payment_date; //결제일(구독시작일)
    private Date subscription_end_date; //구독종료일
    private int max_subscribers; //구독인원(학습가능인원)
    private int appointed_group_num;//그룹 지정된 인원(그룹인원 합)
    
    //학습그룹 등록시 필요
    private String name; //교육자명
    private int payment_no; //결제 번호
}
