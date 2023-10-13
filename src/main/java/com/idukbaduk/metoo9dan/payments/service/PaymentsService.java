package com.idukbaduk.metoo9dan.payments.service;

import com.idukbaduk.metoo9dan.common.entity.Payments;
import com.idukbaduk.metoo9dan.payments.reprository.PaymentsRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private EntityManager entityManager;

    // 구매한 게임컨텐츠에 대한 정보를 가져온다.
    public void paymentsList(){

    }

    //결제하기
    public void buy(){



    }

    // kakao 결제하기
    public String kakaoPayments(){
        try {
            URL kakaoURl = new URL("https://kapi.kakao.com/v1/payment/ready");
            HttpURLConnection connection = (HttpURLConnection) kakaoURl.openConnection();   //서버연결
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization","KakaoAK 952a7b3082d015dcec08556a07b94b78");  // kakao- admin key
            connection.setRequestProperty("Content-type","application/x-www-form-urlencoded;charset=utf-8");
            connection.setDoOutput(true);       //setDoOutput false가 기본이라 설정이 필요. (서버에 전해줄게 있는지? true)
            String information = "cid=TC0ONETIME&partner_order_id=partner_order_id&partner_user_id=partner_user_id&partner_user_id=partner_user_id&item_name=초코파이&quantity=1&total_amount=2200&vat_amount=200&tax_free_amount=0&approval_url=https://developers.kakao.com/success&fail_url=https://developers.kakao.com/fail&cancel_url=https://developers.kakao.com/cancel";
            OutputStream outputStream = connection.getOutputStream();       //값을 준다.
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);   //데이터를 전달한다.
            dataOutputStream.writeBytes(information); // information의 값을 Byte형 변환 해줌
            dataOutputStream.close();   //가지고 있는걸 비우면서 전달 하고 닫아준다.

            int result = connection.getResponseCode(); //결과값
            InputStream getInput;        // 받을 준비.

            // 결과값이 200이면 통신 ok
            if(result == 200){
                getInput =connection.getInputStream();        //결과값을 받는다.
            }else {
                getInput = connection.getErrorStream();      //에러를 받는다.
            }
            InputStreamReader inputStreamReader = new InputStreamReader(getInput);  //받아온 값을 읽는다.
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader); // 받아온 byte값을 문자열로 형변환
            return bufferedReader.readLine();   //문자열로 형변환을 해주고 찍어낸 후 비워낸다.

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



    //결제 내용 저장하기 (DB)
    public void save(Payments payments){


        paymentsRepository.save(payments);
    }



}
