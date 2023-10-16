package com.idukbaduk.metoo9dan.notice.repository;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//Notice Repository Interface
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    //검색 -Containing쓰면 Like 연산자가 가능하다고 함
    //제목만
    Page<Notice> findByNoticeTitleContaining(String keyword, Pageable pageable);
    //내용만
    Page<Notice> findByNoticeContentContaining(String keyword, Pageable pageable);
    //제목+내용
    /*  SELECT *
        FROM notice n
        WHERE n.notice_title LIKE '%내용%'
        or n.notice_content LIKE '%내용%';*/
    Page<Notice> findByNoticeTitleContainingOrNoticeContentContaining(String keyword1, String keyword2, Pageable pageable);

    //faq목록
    Page<Notice> findByNoticeType(String noticeType, Pageable pageable);
    //faq 검색목록
    /*  SELECT *
        FROM notice
        WHERE notice_type = 'FAQ'
        AND (notice_title LIKE '%더미%' OR notice_content LIKE '%더미%');*/
    @Query("SELECT n FROM Notice n WHERE n.noticeType = :type " +
            "AND (n.noticeTitle LIKE %:keyword% OR n.noticeContent LIKE %:keyword%)")
    Page<Notice> findFaqsByTypeAndKeyword(@Param("type") String type, @Param("keyword") String keyword, Pageable pageable);

    //공지 목록 - 관리자 아닌 사람용
    Page<Notice> findByNoticeTypeAndStatus(String noti, String post, Pageable pageable);
}
