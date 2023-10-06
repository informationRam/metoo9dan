package com.idukbaduk.metoo9dan.education.service;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import com.idukbaduk.metoo9dan.education.reprository.ResourcesFilesReprository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Service
@Transactional  //부모entity에 접근할때 필요함
public class ResourcesFilesService {

    @Autowired
    private final ResourcesFilesReprository resourcesFilesRepository;

    @Autowired
    public ResourcesFilesService(ResourcesFilesReprository resourcesFilesRepository) {
        this.resourcesFilesRepository = resourcesFilesRepository;
    }

    public List<ResourcesFiles> getResourcesFilesByResourceNo(Integer resourceNo) {
        return resourcesFilesRepository.findByEducationalResources_ResourceNo(resourceNo);
    }

    public ResourcesFiles getFileByFileNo(Integer fileNo) {
        ResourcesFiles resourcesFiles = resourcesFilesRepository.findById(fileNo).get();
        return resourcesFiles;

    }

    public void deleteFile(Integer fileNo) {
        ResourcesFiles resourcesFiles = resourcesFilesRepository.findById(fileNo).get();
        // 파일 삭제 로직을 구현 (예: 파일 시스템에서 삭제)

        String filePath = "/Users/ryuahn/Desktop/baduk/education/" + resourcesFiles.getCopyFileName();
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete(); // 파일을 삭제
        }
        // 데이터베이스에서 파일 정보를 삭제
        resourcesFilesRepository.delete(resourcesFiles);
    }
}




