package com.idukbaduk.metoo9dan.notice.service;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeFiles;
import com.idukbaduk.metoo9dan.common.entity.NoticeReply;
import com.idukbaduk.metoo9dan.notice.dto.NoticeDTO;
import com.idukbaduk.metoo9dan.notice.dto.NoticeFileDTO;
import com.idukbaduk.metoo9dan.notice.exception.DataNotFoundException;
import com.idukbaduk.metoo9dan.notice.repository.NoticeFilesRepository;
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

//NoticeController의 비즈니스 로직을 처리하는 서비스 클래스
@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeReplyRepository replyRepository;

    //목록조회 - 관리자용
    public Page<Notice> getAdminList(int pageNo, int listSize) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("isImp")); //중요 게시글인 경우 내림차순 정렬.
        sorts.add(Sort.Order.desc("noticeNo")); //pk 기준으로 내림차순 정렬.
        sorts.add(Sort.Order.desc("postDate")); //게시일 기준으로 내림차순 정렬.
        Pageable pageable = PageRequest.of(pageNo, listSize, Sort.by(sorts));

        return noticeRepository.findAll(pageable);
    }

    //공지사항 목록조회 - 관리자 아닌 사람용
    public Page<Notice> getList(int pageNo, int listSize) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("isImp")); //중요 게시글인 경우 내림차순 정렬.
        sorts.add(Sort.Order.desc("noticeNo")); //pk 기준으로 내림차순 정렬.
        sorts.add(Sort.Order.desc("postDate")); //게시일 기준으로 내림차순 정렬.
        Pageable pageable = PageRequest.of(pageNo, listSize, Sort.by(sorts));

        return noticeRepository.findByNoticeTypeAndStatus("noti", "post", pageable);
    }

    //faq 목록조회
    public Page<Notice> getFaqList(int pageNo, int listSize) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("postDate")); //게시일 기준으로 내림차순 정렬.
        Pageable pageable = PageRequest.of(pageNo, listSize, Sort.by(sorts));
        return noticeRepository.findByNoticeTypeAndStatus("faq", "post", pageable);
    }

    //검색 -일반인용검색
    public Page<Notice> search(String searchCategory, String keyword, int pageNo, int listSize) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("noticeNo")); //pk 기준으로 내림차순 정렬.
        sorts.add(Sort.Order.desc("postDate")); //작성일 기준으로 내림차순 정렬.
        Pageable pageable = PageRequest.of(pageNo, listSize, Sort.by(sorts));
        switch (searchCategory){
            case "noticeTitleAndNoticeContent":
                Page<Notice> noticeList = noticeRepository.findByNoticeTitleContainingOrNoticeContentContaining("noti", keyword, keyword, pageable);
                return noticeList;
            case "noticeTitle":
                Page<Notice> searchTitleList = noticeRepository.findByNoticeTitleContaining("noti",keyword, pageable);
                return searchTitleList;
            case "noticeContent":
                Page<Notice> searchContentList = noticeRepository.findByNoticeContentContaining("noti", keyword, pageable);
                return searchContentList;
        }
        return null;
    }

    //FAQ 검색
    public Page<Notice> searchFaq(String keyword, int pageNo, int listSize) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("noticeNo")); //pk 기준으로 내림차순 정렬.
        sorts.add(Sort.Order.desc("postDate")); //작성일 기준으로 내림차순 정렬.
        Pageable pageable = PageRequest.of(pageNo, listSize, Sort.by(sorts));
        Page<Notice> searchFaqList = noticeRepository.findByNoticeTitleContainingOrNoticeContentContaining("faq", keyword, keyword, pageable);
        return searchFaqList;
    }

    //검색 - for Admin 모든 항목별로 검색할 수 있어야 함
    public Page<Notice> searchForAdmin(String noticeType, String status, String searchCategory, String keyword, int pageNo, int listSize) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("noticeNo")); //pk 기준으로 내림차순 정렬.
        sorts.add(Sort.Order.desc("postDate")); //작성일 기준으로 내림차순 정렬.
        Pageable pageable = PageRequest.of(pageNo, listSize, Sort.by(sorts));
        switch (searchCategory){
            case "noticeTitleAndNoticeContent":
                Page<Notice> noticeList = noticeRepository.findByTitleAndContentForAdmin(noticeType, status, keyword, pageable);
                return noticeList;
            case "noticeTitle":
                Page<Notice> searchTitleList = noticeRepository.findByTitleForAdmin(noticeType, status, keyword, pageable);
                return searchTitleList;
            case "noticeContent":
                Page<Notice> searchContentList = noticeRepository.findByContentForAdmin(noticeType, status, keyword, pageable);
                return searchContentList;
        }
        return null;
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
        List<NoticeReply> noticeReply = replyRepository.findByNotice(notice);
        return noticeReply;
    }

    //공지사항 등록처리
    public Notice add(NoticeDTO noticeDTO) {
        Notice notice = new Notice();
        notice.setNoticeType(noticeDTO.getNoticeType());
        notice.setNoticeTitle(noticeDTO.getNoticeTitle());
        notice.setNoticeContent(noticeDTO.getNoticeContent());
        notice.setWriteDate(noticeDTO.getWriteDate());
        notice.setPostDate(noticeDTO.getPostDate());
        notice.setMember(noticeDTO.getMember());
        notice.setStatus(noticeDTO.getStatus());
        notice.setIsImp(noticeDTO.isImp());
        notice.setReadCnt(0);
        return noticeRepository.save(notice);
    }

    //공지사항 삭제처리
    public void delete(Notice notice) {
        noticeRepository.delete(notice);
    }

    //공지사항 수정처리
    public void modify(Notice notice, NoticeDTO noticeDTO) {
        notice.setNoticeType(noticeDTO.getNoticeType());
        notice.setNoticeTitle(noticeDTO.getNoticeTitle());
        notice.setNoticeContent(noticeDTO.getNoticeContent());
        notice.setWriteDate(noticeDTO.getWriteDate());
        notice.setPostDate(noticeDTO.getPostDate());
        notice.setStatus(noticeDTO.getStatus());
        notice.setIsImp(noticeDTO.isImp());
        noticeRepository.save(notice);
    }


}
