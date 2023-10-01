package com.idukbaduk.metoo9dan.notice.repository;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeReplyRepository extends JpaRepository<NoticeReply, Integer> {
    List<NoticeReply> findByNotice(Notice notice);
}
