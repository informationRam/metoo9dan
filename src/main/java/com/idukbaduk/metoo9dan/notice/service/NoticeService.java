package com.idukbaduk.metoo9dan.notice.service;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeReply;
import com.idukbaduk.metoo9dan.notice.exception.DataNotFoundException;
import com.idukbaduk.metoo9dan.notice.repository.NoticeReplyRepository;
import com.idukbaduk.metoo9dan.notice.repository.NoticeRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//NoticeController의 비즈니스 로직을 처리하는 클래스
@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeReplyRepository noticeReplyRepository;

    //목록조회
    public Page<Notice> getList(int pageNo, int listSize) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("isImp")); //중요 게시글인 경우 내림차순 정렬.
        sorts.add(Sort.Order.desc("writeDate")); //작성일 기준으로 내림차순 정렬.
        Pageable pageable = PageRequest.of(pageNo, listSize, Sort.by(sorts));
        //Specification<Notice> specification = search(keyword);

        //return noticeRepository.findAll(specification, pageable);
        return noticeRepository.findAll(pageable);
    }

    //검색
    public Specification<Notice> search(String keyword) {
        return new Specification<Notice>() {
            @Override
            public Predicate toPredicate(Root<Notice> notice, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                query.distinct(true); //중복 제거
                
                return criteriaBuilder.or(criteriaBuilder.like(notice.get("noticeTitle"), "%" + keyword + "%"), //제목
                                          criteriaBuilder.like(notice.get("noticeContent"), "%" + keyword + "%")); //내용
            }
        };
    }

    //상세조회
    public Notice getNotice(Integer noticeNo) {
        Optional<Notice> notice = noticeRepository.findById(noticeNo);
        if(notice.isPresent()){
            return notice.get();
        } else {
            throw new DataNotFoundException("NOTICE NOT FOUND");
        }
    }

    //조회수 증가 처리
    public void readCntUp(Integer noticeNo){
        Optional<Notice> notice = noticeRepository.findById(noticeNo);
        if(notice.isPresent()) {
            Notice getNotice = notice.get();
            getNotice.setReadCnt(getNotice.getReadCnt() + 1); //조회수 증가처리
            this.noticeRepository.save(getNotice);
        }
    }

    //댓글 목록조회
    public List<NoticeReply> getNoticeReply(Notice notice) {
        List<NoticeReply> noticeReply = noticeReplyRepository.findByNotice(notice);
        return noticeReply;
    }
}
