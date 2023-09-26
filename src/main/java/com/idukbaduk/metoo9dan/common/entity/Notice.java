package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="NOTICE")
public class Notice {
    //공지 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="notice_no") //공지 번호
    private Integer noticeNo;

    @Column(name="notice_type") //공지 구분
    private String noticeType;

    @Column(name="notice_title") //공지 제목
    private String noticeTitle;

    @Column(name="notice_content") //공지 내용
    private String noticeContent;

    @Column(name="write_date") //작성일
    private LocalDateTime writeDate;

    @Column(name="post_date") //게시일
    private LocalDateTime postDate;

    @ManyToOne(fetch = FetchType.LAZY) //다대일
    @JoinColumn(name="member_no", referencedColumnName = "member_no")
    private Member member; //공지 작성자

    @Column(name="status") //상태 (게시, 게시 전)
    private String status;

    @Column(name="read_cnt") //조회수
    private int readCnt;

    @Column(name="is_imp") //중요글 여부
    private boolean isImp;

}
