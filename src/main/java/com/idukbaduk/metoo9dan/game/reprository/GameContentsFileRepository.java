package com.idukbaduk.metoo9dan.game.reprository;

import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameContentsFileRepository  extends JpaRepository<GameContentFiles, Integer> {
}
