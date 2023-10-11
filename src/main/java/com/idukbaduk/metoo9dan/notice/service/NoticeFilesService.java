package com.idukbaduk.metoo9dan.notice.service;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeFiles;
import com.idukbaduk.metoo9dan.notice.repository.NoticeFilesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NoticeFilesService {

    private final NoticeFilesRepository filesRepository;

    //파일 목록조회
    public List<NoticeFiles> getFiles(Notice notice){
        List<NoticeFiles> filesList = filesRepository.findByNotice(notice);
        return filesList;
    }

}
