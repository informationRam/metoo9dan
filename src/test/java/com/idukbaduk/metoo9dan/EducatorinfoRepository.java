package com.idukbaduk.metoo9dan;

import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducatorinfoRepository extends JpaRepository<EducatorInfo, Integer> {

}
