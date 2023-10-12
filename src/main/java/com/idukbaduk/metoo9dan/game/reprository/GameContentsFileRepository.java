package com.idukbaduk.metoo9dan.game.reprository;

import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.ResourcesFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameContentsFileRepository  extends JpaRepository<GameContentFiles, Integer> {

    //게임컨텐츠번호로 파일 찾기
//    Optional<GameContentFiles> findByGameContentNo(int gameContentNo);

    List<GameContentFiles> findByGameContents_GameContentNo(Integer gameContentNo);
}
