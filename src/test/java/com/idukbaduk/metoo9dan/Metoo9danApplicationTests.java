package com.idukbaduk.metoo9dan;

import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.GameContentFiles;
import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	EducatorinfoRepository educatorinfoRepository;

	@Autowired
	private EntityManager entityManager;

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
//		GameContents gameContents = new GameContents();
//		gameContents.setGameName("게임제목");
//		gameContents.setDifficulty("beginner");
//		gameContents.setSubscriptionDuration(6);
//		gameContents.setMaxSubscribers(10);
//		gameContents.setOriginal_price(1000.0);
//		gameContents.setDiscountRate(10.0);
//		gameContents.setSalePrice(900.0);
//		gameContents.setPackage_details("패키지내용임");
//		gameContents.setCreationDate(LocalDateTime.now());
//		gameContents.setStatus("Y");
//		gameContents.setContentType("package");
//		gameContentsRepository.save(gameContents);
//
//		GameContentFiles gameContentFiles1 = new GameContentFiles();
//		gameContentFiles1.setGameContents(gameContents);
//		gameContentFiles1.setOriginFileName("원본파일명");
//		gameContentFiles1.setCopyFileName("사본파일명");
//		gameContentsFileRepository.save(gameContentFiles1);


	}


	@Test
	@Transactional
	void gamefileTest() throws Exception {
		Member member = new Member();
		member.setName("이름2");
		member.setTel("010-2222-2222");
		member.setEmail("222@email.com");
		member.setMemberId("id");
		member.setPassword("pw");
		member.setJoinDate(LocalDateTime.now());
		member.setRole("normal");
		member.setGender("F");
		member.setPrivacyConsent(true);
		member.setEmailConsent(true);
		member.setSmsConsent(true);

		// Save the Member entity using memberRepository
		memberRepository.save(member);

		EducatorInfo educatorInfo1 = new EducatorInfo();
		educatorInfo1.setSigungu("시군구");
		educatorInfo1.setSido("시도");
		educatorInfo1.setSchoolName("학교이름");
		educatorInfo1.setMember(member); // Set the Member association
		System.out.println("?");
		// Save the EducatorInfo entity using educatorinfoRepository
		educatorinfoRepository.save(educatorInfo1);

		// No need to close the entityManager here

		// Perform your assertions or further testing as needed

	}


}
