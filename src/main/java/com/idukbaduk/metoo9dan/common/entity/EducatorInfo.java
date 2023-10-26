package com.idukbaduk.metoo9dan.common.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

//교육자 정보 테이블 -PRIMARY KEY (member_no)
@Entity
@Data
@Table(name="educator_info")
public class EducatorInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "educator_info_no")
    private Integer educatorInfoNo;

    // @MapsId //@MapsId 는 @id로 지정한 컬럼에 @OneToOne 이나 @ManyToOne 관계를 매핑시키는 역할 :해당 엔티티의 PK가 다른 엔티티의 PK와 동일하게 설정
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", referencedColumnName = "member_no",unique = true)
    @ToString.Exclude
    private Member member;

    @Column
    private String sido;         //varchar(20)    NOT NULL    COMMENT '시도',

    @Column
    private String sigungu;      //varchar(20)    NOT NULL    COMMENT '시군구',

    @Column(name="school_name")
    private String schoolName;  //varchar(20)    NOT NULL    COMMENT '소속명',

}
