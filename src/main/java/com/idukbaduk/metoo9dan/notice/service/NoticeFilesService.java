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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    //파일사이즈확인
    public boolean fileSizeOk(MultipartFile multipartFile){
        int maxSize = 31457280; //30MB
        logger.info("fileSize: " +multipartFile.getSize());
        if(multipartFile.getSize() > maxSize){
            return false;
        }
        return true;
    }

    //파일확장자확인
    public boolean fileTypeOk(MultipartFile multipartFile){
        if(!multipartFile.getContentType().contains("image")){
            return false;
        }
        return true;
    }

    //파일 업로드를 처리하는 메소드 (물리적 폴더와 파일 생성 및 DB에 저장)
    public void fileUpload(String uploadFolder, Notice notice, MultipartFile multipartFile, List<NoticeFileDTO> list, RedirectAttributes redirectAttributes) {
        // getFolder(): 년/월/일 폴더 생성
        String uploadFolderPath = getFolder();
        File uploadPath = new File(uploadFolder, uploadFolderPath);
        if(uploadPath.exists() == false){
            uploadPath.mkdirs();
        }

        //DTO에 담아 간다
        NoticeFileDTO fileDTO = new NoticeFileDTO(); //DTO객체 생성
        fileDTO.setNotice(notice); //방금 생성한 공지번호 저장

        String uploadFileName = multipartFile.getOriginalFilename();
        fileDTO.setOriginFileName(uploadFileName); //원본파일명 저장

        //파일이름 중복방지를 위한 UUID
        UUID uuid =UUID.randomUUID();
        uploadFileName = uuid.toString()+"_"+uploadFileName;

        try {
            File saveFile = new File(uploadPath, uploadFileName);
            multipartFile.transferTo(saveFile);
            fileDTO.setUuid(uuid.toString()); //사본파일명 저장
            fileDTO.setUploadPath(uploadFolderPath+""); //C:/upload이하 파일경로 저장
            list.add(fileDTO);
        } catch (IOException e) {
            logger.error(e.getMessage());
            redirectAttributes.addFlashAttribute("msg", "공지 등록하는 중에 에러 발생");
        }//end catch
    }

    //중복된 이름의 파일처리
    //년/월/일 폴더 생성
    public String getFolder(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String str = sdf.format(date);
        return str.replace("-", "/");
    }

}
