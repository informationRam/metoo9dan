package com.idukbaduk.metoo9dan;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.beans.Transient;

@SpringBootTest
class Metoo9danApplicationTests {

	@Autowired
	InfoRepository infoRepository;

	@Test
	void DBConnectionTest() throws Exception {
		Info info = new Info();
		info.setText("test1");
		infoRepository.save(info);

		//Info findInfo = infoRepository.find();

		//Assertions.assertEquals(findInfo.getId(), info.getId());
		//Assertions.assertEquals(findInfo.getText(), info.getText());

	}

}
