package com.idukbaduk.metoo9dan;

import com.idukbaduk.metoo9dan.common.entity.*;
import com.idukbaduk.metoo9dan.notice.repository.NoticeRepository;
import com.idukbaduk.metoo9dan.qna.repository.QuestionRepository;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import java.beans.Transient;
import java.time.LocalDateTime;

@SpringBootTest
class Metoo9danApplicationTests {

	@Autowired
	InfoRepository infoRepository;
	@Autowired
	NoticeRepository noticeRepository;

	@Autowired
	QuestionRepository questionRepository;

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

	@Autowired
	private MemberService memberService;

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

	@Test
	public void testRegisterEducator() {
		// 교육자 정보 생성
		Member member = new Member();
		member.setName("안찌구");
		member.setTel("010-1234-5678");
		member.setEmail("1235@email.com");
		member.setMemberId("zzigu");
		member.setPassword("1234");
		member.setJoinDate(LocalDateTime.now());
		member.setRole("normal");
		member.setGender("F");
		member.setPrivacyConsent(true);
		member.setEmailConsent(true);
		member.setSmsConsent(true);
		member.setMemberMemo("회원관리시 메모 남기는 영역입니다");
		// 나머지 필드 설정

		EducatorInfo educatorInfo = new EducatorInfo();
		educatorInfo.setSido("서울");
		educatorInfo.setSigungu("강남구");
		educatorInfo.setSchoolName("중앙정보");

		// 교육자 회원 가입
		memberService.registerEducator(member, educatorInfo);

		// 회원과 교육자 정보가 저장되었는지 확인
		Member savedMember = memberRepository.findById(member.getMemberNo()).orElse(null);
		EducatorInfo savedEducatorInfo = educatorinfoRepository.findById(member.getMemberNo()).orElse(null);

		assertNotNull(savedMember);
		assertNotNull(savedEducatorInfo);

		// 이후 검증 로직 추가
	}

	@Test
	public void insertNotice(){
		Member member = new Member();
		member.setMemberNo(1);
		for(int i=0; i<100; i++){
			LocalDateTime now = LocalDateTime.now();
			Notice notice = new Notice();
			notice.setNoticeType("noti");
			notice.setNoticeTitle(i+"번째 test제목");
			notice.setNoticeContent(i+"번째 test내용");
			notice.setStatus("post");
			notice.setIsImp(false);
			notice.setWriteDate(now);
			notice.setPostDate(now);
			notice.setReadCnt(0);
			notice.setMember(member);
			noticeRepository.save(notice);
		}
	}
	@Test
	public void insertQuestion(){
		Member member = new Member();
		member.setMemberNo(1);
		for(int i=0; i<10; i++){
			LocalDateTime now = LocalDateTime.now();
			QnaQuestions questions = new QnaQuestions();
			questions.setQuestionTitle(i+"번째 test제목");
			questions.setQuestionContent(i+"번째 test내용");
			questions.setIsAnswered("N");
			questions.setWriteDate(now);
			questions.setMember(member);
			questionRepository.save(questions);
		}
	}
}
