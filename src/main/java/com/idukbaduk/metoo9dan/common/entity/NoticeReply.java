package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="NOTICE_REPLY")
public class NoticeReply {
    //공지 댓글 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="notice_reply_no")
    private Integer noticeReplyNo; //공지 댓글 번호

    @Column(name="content")
    private String content; //내용

    @ManyToOne(fetch = FetchType.LAZY) //다대일
    @JoinColumn(name="member_no", referencedColumnName = "member_no")
    private Member member; //댓글 작성자

    @Column(name="write_date")
    private LocalDateTime writeDate; //작성일

    @ManyToOne(fetch = FetchType.LAZY) //다대일
    @JoinColumn(name="notice_no", referencedColumnName = "notice_no")
    private Notice notice; //공지번호
}
