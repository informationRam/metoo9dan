package com.idukbaduk.metoo9dan.education.reprository;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<EducationalResources, Integer> {
    // like 사용
    List<EducationalResources> findByResourceNameContains(String ResourceName);

    //게임콘텐츠no 값으로 EducationalResources값 가져오는법
    List<EducationalResources> findByGameContents_GameContentNo(Integer gameContentNo);

    // 자료구분으로 페이지네이션
    Page<EducationalResources> findByResourceCate(String resourceCate,Pageable pageable);

    // 제목으로
    Page<EducationalResources> findByResourceNameContaining(String resourceName, Pageable pageable);

    Page<EducationalResources> findByGameContentsGameContentNo(Integer gameContentNo,Pageable pageable);

    Page<EducationalResources> findByGameContentsGameContentNoAndResourceCateContaining(Integer gameContentNo, String resourceCate, Pageable pageable);



}
