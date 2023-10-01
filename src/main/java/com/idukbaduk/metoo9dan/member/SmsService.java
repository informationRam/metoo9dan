package com.idukbaduk.metoo9dan.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idukbaduk.metoo9dan.member.dto.MessageDTO;
import com.idukbaduk.metoo9dan.member.dto.SmsRequestDTO;
import com.idukbaduk.metoo9dan.member.dto.SmsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class SmsService {

    //환경설정 변수
    @Value("${naver-cloud-sms.accessKey}")
    private String accessKey;

    @Value("${naver-cloud-sms.secretKey}")
    private String secretKey;

    @Value("${naver-cloud-sms.serviceId}")
    private String serviceId;

    @Value("${naver-cloud-sms.senderPhone}")
    private String phone;

    //secureRandom으로 보안성 강화 인증번호 생성
    private final SecureRandom secureRandom = new SecureRandom();
    private final String numericCharacters = "0123456789";


    public String makeSignature(Long time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String space = " ";					// one space
        String newLine = "\n";					// new line
        String method = "POST";					// method
        String url = "/sms/v2/services/" + this.serviceId+"/messages";	// url (include query string)
        String timestamp = time.toString();			// current timestamp
        String accessKey = this.accessKey;			// access key id (from portal or Sub Account)
        String secretKey = this.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }

    public SmsResponseDTO sendSms(String to) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

        // 1. Form에서 받은 번호를 messageDto의 to 필드에 저장
        MessageDTO messageDto = new MessageDTO();
        messageDto.setTo(to);

        // 2. messageDto의 content 영역에 6자리 난수를 생성해 저장
        String randomContent = generateRandomContent();

        // SMS 내용을 직접 설정: 텍스트와 난수 합치기
        String smsContent = "[나도9단] 인증번호 [" + randomContent  + "]를 입력해주세요";

//        // 여럿에 동일한 sms 내용 보낼때 사용
//        List<MessageDTO> messages = new ArrayList<>();
//        messages.add(messageDto);

        //발송 내용 설정: DTO에서 선언한 @Builder 사용
        SmsRequestDTO request = SmsRequestDTO.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(phone)
                .content(smsContent) // 직접 설정한 SMS 내용 사용
                .messages(Collections.singletonList(messageDto))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        SmsResponseDTO response = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ serviceId +"/messages"), httpBody, SmsResponseDTO.class);

        return response;
    }
    private String generateRandomContent() {
        // 난수 생성 로직 구현
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(numericCharacters.length());
            sb.append(numericCharacters.charAt(randomIndex));
        }
        return sb.toString();
    }

   /*     // 예를 들어, 6자리 난수 생성
        Random random = new Random();
        int randomValue = 100000 + random.nextInt(900000); // 100000부터 999999까지의 난수 생성
        return String.valueOf(randomValue);
    }*/
}
