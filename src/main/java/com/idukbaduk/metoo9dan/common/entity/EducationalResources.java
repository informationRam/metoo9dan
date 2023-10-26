package com.idukbaduk.metoo9dan.common.entity;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//교육자료테이블 - PRIMARY KEY (resource_no)
@Entity
@Data
@Table(name="educational_resources")
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userCode")
public class EducationalResources {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="resource_no")
    private Integer resourceNo;      //int  NOT NULL    AUTO_INCREMENT COMMENT '교육자료 번호'

    @Column(name="resource_name")
    private String resourceName;    //varchar(100) NOT NULL    COMMENT '교육자료명',

    @Column(name="resource_cate")
    private String resourceCate;    //varchar(50) NOT NULL    COMMENT '자료 구분',

    @Column(name="file_type")
    private String fileType;        //varchar(50) NOT NULL    COMMENT '자료 유형',

    private String status;         //text  NOT NULL    COMMENT '파일상태',

    @Column(name="service_type")
    private String serviceType;     // NOT NULL    COMMENT '서비스 구분',

    @Column
    private String description;      //text NOT NULL    COMMENT '자료내용',

    @Column(name="creation_date",columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDate;    //datetime  NOT NULL    COMMENT '등록일',

    @ManyToOne(fetch = FetchType.LAZY) // 게임콘텐츠-다대일 관계
    @JoinColumn(name = "game_content_no", referencedColumnName = "game_content_no") // 외래 키 설정
    private GameContents gameContents;

    @OneToOne(mappedBy = "educationalResources", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ResourcesFiles resourcesFilesList;

    //재귀 방지 gameContents를 출력하지 않도록 함.
    @Override
    public String toString() {
        return "EducationalResources{" +
                "resourceNo=" + resourceNo +
                ", resourceName='" + resourceName + '\'' +
                ", resourceCate='" + resourceCate + '\'' +
                ", fileType='" + fileType + '\'' +
                ", status='" + status + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", description='" + description + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceNo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EducationalResources other = (EducationalResources) obj;
        return Objects.equals(resourceNo, other.resourceNo);
    }
}
