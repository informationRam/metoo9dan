package com.idukbaduk.metoo9dan.notice.service;

import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeReply;
import com.idukbaduk.metoo9dan.notice.repository.NoticeReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

//NoticeReplyController의 비즈니스 로직을 처리하는 서비스 클래스
@RequiredArgsConstructor
@Service
public class NoticeReplyService {
    private final NoticeReplyRepository replyRepository;

    //메소드
    // 댓글 등록처리
    public void add(Notice notice, String content, Member member) {
        NoticeReply noticeReply = new NoticeReply();
        noticeReply.setContent(content);
        noticeReply.setWriteDate(LocalDateTime.now()); //작성일
        noticeReply.setNotice(notice);
        noticeReply.setMember(member);
        replyRepository.save(noticeReply); //insert처리
    }
}
