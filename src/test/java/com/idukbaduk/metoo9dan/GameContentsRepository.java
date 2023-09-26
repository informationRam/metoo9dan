package com.idukbaduk.metoo9dan;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Repository;

@Repository
public interface GameContentsRepository extends JpaRepository<GameContents, Integer> {

}
