package com.idukbaduk.metoo9dan.member.repository;

import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducatorinfoRepository extends JpaRepository<EducatorInfo, Integer> {

}