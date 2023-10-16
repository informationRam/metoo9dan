package com.idukbaduk.metoo9dan.education.reprository;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<EducationalResources, Integer> {
    // like 사용
    List<EducationalResources> findByResourceNameContains(String ResourceName);

    //게임콘텐츠no 값으로 EducationalResources값 가져오는법
    List<EducationalResources> findByGameContents_GameContentNo(Integer gameContentNo);
}
