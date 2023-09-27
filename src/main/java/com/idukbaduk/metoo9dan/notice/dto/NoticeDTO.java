package com.idukbaduk.metoo9dan.notice.dto;

import com.idukbaduk.metoo9dan.common.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoticeDTO {
    private int noticeNo;               //공지 번호
    private String noticeType;          //공지 타입(공지사항, 자주묻는질문)
    private String noticeTitle;         //공지 제목
    private String noticeContent;       //공지 내용
    private LocalDateTime writeDate;    //작성일
    private LocalDateTime postDate;     //게시일
    private int memberNo;               //공지 작성자
    private String status;              //상태(게시, 예약)
    private int readCnt;                //조회수
    private boolean isImp;              //중요게시글 여부
}
