package com.idukbaduk.metoo9dan.notice.service;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeFiles;
import com.idukbaduk.metoo9dan.notice.exception.DataNotFoundException;
import com.idukbaduk.metoo9dan.notice.repository.NoticeFilesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NoticeFilesService {

    private final NoticeFilesRepository filesRepository;

    //파일 목록조회
    public List<NoticeFiles> getFiles(Notice notice){
        List<NoticeFiles> filesList = filesRepository.findByNotice(notice);
        return filesList;
    }

    //삭제를 위한 파일 조회
    public NoticeFiles selectFile(Integer fileNo) {
        Optional<NoticeFiles> noticeFiles = filesRepository.findById(fileNo);
        if(noticeFiles.isPresent()){
            return noticeFiles.get();
        } else {
            throw new DataNotFoundException("FILE NOT FOUND");
        }
    }

    //DB에서 삭제
    public void delete(NoticeFiles noticeFiles) {
        filesRepository.delete(noticeFiles);
    }
}
