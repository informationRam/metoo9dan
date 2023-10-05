package com.idukbaduk.metoo9dan.education.service;

import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import com.idukbaduk.metoo9dan.education.reprository.ResourcesFilesReprository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        resourcesFilesRepository.delete(resourcesFiles);
    }
}




