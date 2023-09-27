package com.idukbaduk.metoo9dan.notice.service;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//NoticeController의 비즈니스 로직을 처리하는 클래스
@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    //목록조회
    public Page<Notice> getList(int pageNo, int listSize) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("writeDate")); //작성일 기준으로 내림차순 정렬.
        Pageable pageable = PageRequest.of(pageNo, listSize, Sort.by(sorts));

        return noticeRepository.findAll(pageable);
    }
}
