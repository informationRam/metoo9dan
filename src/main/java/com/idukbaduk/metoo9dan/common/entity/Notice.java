package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
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
    private Integer readCnt;

    @Column(name="is_imp") //중요글 여부
    private Boolean isImp;

    //댓글수를 출력하기 위한 메소드 선언 -> DB에 영향없음.
    public Integer getReplyCnt(){
        if(noticeReplies != null){
            return noticeReplies.size();
        } else{
            return 0;
        }
    }
    //첨부파일 유무를 확인하기 위한 메소드
    public Boolean haveAttachFiles(){
        return noticeFiles != null && !noticeFiles.isEmpty();
    }
    //이 해당필드가 NoticeReply 엔티티의 'notice 필드'에 의해 매핑된다는 의미.
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private List<NoticeReply> noticeReplies;

    //이 해당필드가 NoticeFiles 엔티티의 'notice 필드'에 의해 매핑된다는 의미.
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NoticeFiles> noticeFiles;
}
