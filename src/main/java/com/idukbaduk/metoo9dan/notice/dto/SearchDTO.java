package com.idukbaduk.metoo9dan.notice.dto;

import lombok.Data;
import org.springframework.core.annotation.MergedAnnotations;

//공지사항 목록에서 검색 및 페이지네이션과 관련 담당 DTO

@Data
public class SearchDTO {

    private int pageNo; //현재 페이지 번호. view Query=page
    private int listSize; //페이지당 출력할 데이터 개수
    private int pageSize; //화면 하단에 출력할 페이지 사이즈
    private String keyword; //검색 키워드
    private String searchType; //검색 유형

    public SearchDTO() {
        this.pageNo = 1;
        this.listSize = 10;
        this.pageSize = 5;
    }

    public SearchDTO(int listSize){
        this.pageNo = 1;
        this.listSize = listSize;
        this.pageSize = 5;
    }

    public int getOffset(){
        return (pageNo - 1) * listSize;
    }
}
