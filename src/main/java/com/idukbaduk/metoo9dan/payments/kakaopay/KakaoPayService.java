package com.idukbaduk.metoo9dan.payments.kakaopay;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.member.service.MemberServiceImpl;
import com.idukbaduk.metoo9dan.payments.service.PaymentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class KakaoPayService {

        static final String cid = "TC0ONETIME"; // 가맹점 테스트 코드
        static final String admin_Key = "952a7b3082d015dcec08556a07b94b78"; // 공개 조심! 본인 애플리케이션의 어드민 키를 넣어주세요
        private KakaoReadyResponse kakaoReady;
        private final PaymentsService paymentsService;
        private final MemberServiceImpl memberService;


        public KakaoReadyResponse kakaoPayReady(String item_name, String totalAmount, String memberId) {

            // 카카오페이 요청 양식
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("cid", cid);
            parameters.add("partner_order_id", "가맹점 주문 번호");
            parameters.add("partner_user_id", memberId); //"가맹점 회원 ID"
            parameters.add("item_name", item_name);
            parameters.add("quantity", "1" );
            parameters.add("total_amount",totalAmount);
            parameters.add("vat_amount", "100");
            parameters.add("tax_free_amount", "0");
            parameters.add("approval_url", "http://localhost/payments/success"); // 성공 시 redirect url
            parameters.add("cancel_url", "http://localhost/payments/cancel"); // 취소 시 redirect url
            parameters.add("fail_url", "http://localhost/payments/fail"); // 실패 시 redirect url
            // 파라미터, 헤더
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

            // 외부에 보낼 url
            RestTemplate restTemplate = new RestTemplate();

            kakaoReady = restTemplate.postForObject(
                    "https://kapi.kakao.com/v1/payment/ready",
                    requestEntity,
                    KakaoReadyResponse.class);
            return kakaoReady;
        }
    /**
     * 결제 완료 승인
     */
    public KakaoApproveResponse approveResponse(String pgToken, Member member, List<GameContents> selectedGameContents, String pay, String memberId) {

        // 카카오 요청
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("tid", kakaoReady.getTid());
        parameters.add("partner_order_id", "가맹점 주문 번호");
        parameters.add("partner_user_id", memberId); //"가맹점 회원 ID"
        parameters.add("pg_token", pgToken);

        // 파라미터, 헤더
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        // 외부에 보낼 url
        RestTemplate restTemplate = new RestTemplate();

        KakaoApproveResponse approveResponse = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/approve",
                requestEntity,
                KakaoApproveResponse.class);

        paymentsService.save(selectedGameContents, member, pay);
        memberService.userUpdate(member);
        return approveResponse;
    }

        /**
         * 카카오 요구 헤더값
         */
        private HttpHeaders getHeaders() {
            HttpHeaders httpHeaders = new HttpHeaders();

            String auth = "KakaoAK " + admin_Key;

            httpHeaders.set("Authorization", auth);
            httpHeaders.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            return httpHeaders;
        }
}
