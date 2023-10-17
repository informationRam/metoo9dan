package com.idukbaduk.metoo9dan.notice.controller;

import com.idukbaduk.metoo9dan.common.entity.NoticeFiles;
import com.idukbaduk.metoo9dan.notice.service.NoticeFilesService;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Log4j2
@RequiredArgsConstructor
@RequestMapping("/notice")
@Controller
public class NoticeFileController {
    //looger
    Logger logger = LoggerFactory.getLogger(NoticeController.class);
    private final NoticeFilesService filesService;

    // 파일 다운로드
    @GetMapping(value="/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> download(String fileName){
        Resource resource = new FileSystemResource("C:\\upload\\"+fileName);
        if(resource.exists()==false){ //존재하지 않으면 에러처리
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String resourceName = resource.getFilename();

        //uuid 지우기
        String resourceOriginalName = resourceName.substring(resourceName.indexOf("_")+1);

        HttpHeaders headers = new HttpHeaders();
        try{
            String downloadName = new String(resourceOriginalName.getBytes("UTF-8"), "ISO-8859-1" );
            logger.info("downloadName: "+downloadName);
            headers.add("Content-Disposition", "attachment; filename="+downloadName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/deleteFile/{fileNo}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> deleteFile(@PathVariable("fileNo")Integer fileNo){
        //fileName = 경로/uuid_originName
        logger.info("deleteFile()진입, 삭제하려는 fileNo: "+fileNo);

        //해당 파일 조회
        NoticeFiles noticeFiles = filesService.selectFile(fileNo);
        if(noticeFiles!=null){
            //해당 파일 삭제
            //1) 디비삭제
            filesService.delete(noticeFiles);

            //2) 물리파일삭제~~~~~~~~~~~~~~~
        }


        //에러난 경우 에러응답 보내기

        //정상 처리된 경우 응답 보내기
        return new ResponseEntity<Resource>(HttpStatus.OK);

    }
}
