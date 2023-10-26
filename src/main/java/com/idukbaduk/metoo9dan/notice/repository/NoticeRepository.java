package com.idukbaduk.metoo9dan.notice.repository;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

//Notice Repository Interface
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    //검색 -Containing쓰면 Like 연산자가 가능하다고 함
    //제목만
    @Query("SELECT n FROM Notice n WHERE n.noticeType = :type " +
            "AND n.status = 'post' " +
            "AND n.noticeTitle LIKE %:keyword%")
    Page<Notice> findByNoticeTitleContaining(@Param("type") String type,
                                             @Param("keyword") String keyword,
                                             Pageable pageable);
    //내용만
    @Query("SELECT n FROM Notice n WHERE n.noticeType = :type " +
            "AND n.status = 'post' " +
            "AND n.noticeContent LIKE %:keyword%")
    Page<Notice> findByNoticeContentContaining(@Param("type") String type,
                                               @Param("keyword") String keyword,
                                               Pageable pageable);
    //제목+내용
    @Query("SELECT n FROM Notice n WHERE n.noticeType = :type " +
            "AND n.status = 'post' " +
            "AND (n.noticeTitle LIKE %:keyword% OR n.noticeContent LIKE %:keyword%)")
    Page<Notice> findByNoticeTitleContainingOrNoticeContentContaining(@Param("type") String type,
                                                                      @Param("keyword") String keyword,
                                                                      @Param("keyword") String keyword1,
                                                                      Pageable pageable);

    //공지 및 faq목록 - 관리자 아닌 사람용
    @Query("SELECT n FROM Notice n WHERE n.noticeType = :type " +
            "AND n.status = :status")
    Page<Notice> findByNoticeTypeAndStatus(@Param("type") String type, @Param("status")String status, Pageable pageable);

    //관리자용 검색
    @Query("SELECT n FROM Notice n WHERE n.noticeType = :type " +
            "AND n.status = :status " +
            "AND (n.noticeTitle LIKE %:keyword% OR n.noticeContent LIKE %:keyword%)")
    Page<Notice> findByTitleAndContentForAdmin(@Param("type") String type, @Param("status") String status, @Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT n FROM Notice n WHERE n.noticeType = :type " +
            "AND n.status = :status " +
            "AND n.noticeTitle LIKE %:keyword%")
    Page<Notice> findByTitleForAdmin(@Param("type") String type, @Param("status") String status, @Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT n FROM Notice n WHERE n.noticeType = :type " +
            "AND n.status = :status " +
            "AND n.noticeContent LIKE %:keyword%")
    Page<Notice> findByContentForAdmin(@Param("type") String type, @Param("status") String status, @Param("keyword") String keyword, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Notice n SET n.status = 'post' WHERE n.postDate = :postDate")
    void updateStatusForPostDateEqual(@Param("postDate") LocalDateTime postDate);
}
