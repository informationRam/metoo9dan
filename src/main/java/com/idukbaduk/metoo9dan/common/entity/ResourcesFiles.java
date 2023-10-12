package com.idukbaduk.metoo9dan.common.entity;


import jakarta.persistence.*;
import lombok.Data;

// 교육자료 파일 테이블 - PRIMARY KEY (file_no)
@Entity
@Data
@Table(name = "resources_files")
public class ResourcesFiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="file_no")
    private Integer fileNo;           //int  NOT NULL    AUTO_INCREMENT COMMENT '파일 번호',

    @Column(name="origin_file_name")
    private String originFileName;  //varchar(100)    NOT NULL    COMMENT '원본파일명',

    @Column(name="copy_file_name")
    private String copyFileName;    //varchar(100)    NOT NULL    COMMENT '사본파일명',

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계
    @JoinColumn(name = "resource_no", referencedColumnName = "resource_no") // 외래 키 설정
    private EducationalResources educationalResources;



}
