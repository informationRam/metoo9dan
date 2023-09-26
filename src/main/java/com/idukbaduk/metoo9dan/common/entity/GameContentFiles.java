package com.idukbaduk.metoo9dan.common.entity;

import jakarta.persistence.*;
import lombok.Data;

//게임콘텐츠 파일 테이블 - PRIMARY KEY (file_no)
@Entity
@Data
@Table(name = "game_content_files")
public class GameContentFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="file_no")
    private Integer fileNo;           //int             NOT NULL    AUTO_INCREMENT COMMENT '파일 번호',

    @Column(name="origin_file_name")
    private String originFileName;  //varchar(100)    NOT NULL    COMMENT '원본파일명',

    @Column(name="copy_file_name")
    private String copyFileName;    //varchar(100)    NOT NULL    COMMENT '사본파일명',

    @ManyToOne(fetch = FetchType.LAZY) // 게임콘텐츠-다대일 관계
    @JoinColumn(name = "game_content_no", referencedColumnName = "game_content_no") // 외래 키 설정
    private GameContents gameContents;

}
