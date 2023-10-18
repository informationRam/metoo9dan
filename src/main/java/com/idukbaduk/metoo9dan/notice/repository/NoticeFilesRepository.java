package com.idukbaduk.metoo9dan.notice.repository;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeFiles;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeFilesRepository extends JpaRepository<NoticeFiles, Integer> {
    //공지 번호로 파일목록 조회
    List<NoticeFiles> findByNotice(Notice notice);
}
