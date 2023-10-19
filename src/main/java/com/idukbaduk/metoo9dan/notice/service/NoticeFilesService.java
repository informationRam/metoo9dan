package com.idukbaduk.metoo9dan.notice.service;

import com.idukbaduk.metoo9dan.common.entity.Notice;
import com.idukbaduk.metoo9dan.common.entity.NoticeFiles;
import com.idukbaduk.metoo9dan.notice.controller.NoticeController;
import com.idukbaduk.metoo9dan.notice.dto.NoticeFileDTO;
import com.idukbaduk.metoo9dan.notice.exception.DataNotFoundException;
import com.idukbaduk.metoo9dan.notice.repository.NoticeFilesRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NoticeFilesService {
    //looger
    Logger logger = LoggerFactory.getLogger(NoticeController.class);

    private final NoticeFilesRepository filesRepository;

    //파일 목록조회
    public List<NoticeFiles> getFiles(Notice notice){
        List<NoticeFiles> filesList = filesRepository.findByNotice(notice);
        return filesList;
    }

    //첨부파일 저장처리
    public void addFiles(List<NoticeFileDTO> list) {
        for (int i = 0; i < list.size(); i++) {
            NoticeFiles noticeFiles = new NoticeFiles();
            noticeFiles.setNotice(list.get(i).getNotice());
            noticeFiles.setOriginFileName(list.get(i).getOriginFileName());
            noticeFiles.setCopyFileName(list.get(i).getUuid());
            noticeFiles.setFileUrl(list.get(i).getUploadPath());
            filesRepository.save(noticeFiles);
        }
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
    public void deleteOnDB(NoticeFiles noticeFiles) {
        logger.info("fileService진입. deleteOnDB진행");
        filesRepository.delete(noticeFiles);
        logger.info("fileService진입. deleteOnDB성공");
    }

    //디스크에서 삭제
    // fileName = 경로/uuid_originName
    public ResponseEntity<String> deleteOnDisk(NoticeFiles noticeFiles) {
        String fileName = noticeFiles.getFileUrl()+"/"+noticeFiles.getCopyFileName()+"_"+noticeFiles.getOriginFileName();
        logger.info("deleteOnDist진입. upload폴더에 저장된 fileName: "+fileName);

        File file;
        try{
            file = new File("C:\\upload\\"+ URLDecoder.decode(fileName, "UTF-8"));
            file.delete();
            logger.info("Dist에 있는 파일 삭제완료");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>("deleted", HttpStatus.OK);
    }


}
