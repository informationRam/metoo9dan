package com.idukbaduk.metoo9dan.notice.dto;

import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.Notice;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

//공지사항 첨부파일 데이터 관련 DTO
@Data
public class NoticeFileDTO {
    private Notice notice; //공지 번호
    private List<String> originFileName; //원본파일명
    private List<String> copyFileName; //사본파일명
    private List<String> fileUrl; //파일주소
    private List<MultipartFile> noticeFiles; //파일들

}
