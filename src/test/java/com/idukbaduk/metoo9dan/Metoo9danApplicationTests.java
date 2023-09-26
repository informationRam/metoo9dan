package com.idukbaduk.metoo9dan;

import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.beans.Transient;
import java.time.LocalDateTime;

@SpringBootTest
class Metoo9danApplicationTests {

	@Autowired
	InfoRepository infoRepository;

	@Autowired
	GameContentsRepository gameContentsRepository;

	@Autowired
	GameContentsFileRepository gameContentsFileRepository;

	@Test
	void DBConnectionTest() throws Exception {
//		Info info = new Info();
//		info.setText("test1");
//		infoRepository.save(info);

		//Info findInfo = infoRepository.find();

		//Assertions.assertEquals(findInfo.getId(), info.getId());
		//Assertions.assertEquals(findInfo.getText(), info.getText());

	}

	@Test
	void gameTest() throws Exception {
		GameContents gameContents = new GameContents();
		gameContents.setGameName("게임제목");
		gameContents.setDifficulty("beginner");
		gameContents.setSubscriptionDuration(6);
		gameContents.setMaxSubscribers(10);
		gameContents.setOriginal_price(1000.0);
		gameContents.setDiscountRate(10.0);
		gameContents.setSalePrice(900.0);
		gameContents.setPackage_details("패키지내용임");
		gameContents.setCreationDate(LocalDateTime.now());
		gameContents.setStatus("Y");
		gameContents.setContentType("package");
		gameContentsRepository.save(gameContents);

		GameContentFiles gameContentFiles1 = new GameContentFiles();
		gameContentFiles1.setGameContents(gameContents);
		gameContentFiles1.setOriginFileName("원본파일명");
		gameContentFiles1.setCopyFileName("사본파일명");
		gameContentsFileRepository.save(gameContentFiles1);


	}


	@Test
	void gamefileTest() throws Exception {


		gameContentsFileRepository.findById(1);

	}

}
