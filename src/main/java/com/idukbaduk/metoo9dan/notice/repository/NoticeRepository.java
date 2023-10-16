package com.idukbaduk.metoo9dan.notice.repository;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//Notice Repository Interface
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    Page<Notice> findAll(Specification<Notice> specification, Pageable pageable);

    //검색 -Containing쓰면 Like 연산자가 가능하다고 함
    Page<Notice> findByNoticeTitleContaining(String keyword, Pageable pageable);
}
