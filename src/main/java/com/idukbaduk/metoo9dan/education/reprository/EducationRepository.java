package com.idukbaduk.metoo9dan.education.reprository;

import com.idukbaduk.metoo9dan.common.entity.EducationalResources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<EducationalResources, Integer> {
}
