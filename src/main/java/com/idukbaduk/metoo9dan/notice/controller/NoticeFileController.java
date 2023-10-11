package com.idukbaduk.metoo9dan.notice.controller;

import groovy.util.logging.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Log4j2
@RequestMapping("/notice")
@Controller
public class NoticeFileController {
    //looger
    Logger logger = LoggerFactory.getLogger(NoticeController.class);

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
}
