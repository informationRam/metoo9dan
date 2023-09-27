package com.idukbaduk.metoo9dan.notice.repository;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//Notice Repository Interface
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
}
