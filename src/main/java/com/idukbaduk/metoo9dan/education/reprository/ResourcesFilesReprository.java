package com.idukbaduk.metoo9dan.education.reprository;

import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourcesFilesReprository extends JpaRepository<ResourcesFiles, Integer> {
    List<ResourcesFiles> findByEducationalResources_ResourceNo(Integer resourceNo);

}
