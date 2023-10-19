package com.idukbaduk.metoo9dan.notice.repository;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface NoticeReplyRepository extends JpaRepository<NoticeReply, Integer> {
    //공지 번호로 댓글목록 조회
    List<NoticeReply> findByNotice(Notice notice);
}
