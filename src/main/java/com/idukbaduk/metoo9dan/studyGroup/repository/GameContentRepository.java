package com.idukbaduk.metoo9dan.studyGroup.repository;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameContentRepository extends JpaRepository<GameContents,Integer> {
}
