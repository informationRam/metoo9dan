package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="NOTICE_FILES")
public class NoticeFiles {
    //공지사항 첨부파일 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="file_no")
    private Integer fileNo; //파일 번호

    @ManyToOne(fetch = FetchType.LAZY) //다대일
    @JoinColumn(name="notice_no", referencedColumnName = "notice_no")
    private Notice notice; //공지 번호

    @Column(name="origin_file_name")
    private String origin_file_name; //원본파일명

    @Column(name="copy_file_name")
    private String copy_file_name; //사본파일명
}
