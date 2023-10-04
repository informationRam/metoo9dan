package com.idukbaduk.metoo9dan.game.reprository;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<GameContents, Integer> {


}
