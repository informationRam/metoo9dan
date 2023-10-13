package com.idukbaduk.metoo9dan.payments.controller;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.game.service.GameFilesService;
import com.idukbaduk.metoo9dan.game.service.GameService;
import com.idukbaduk.metoo9dan.payments.kakaopay.KakaoApproveResponse;
import com.idukbaduk.metoo9dan.payments.kakaopay.KakaoPayService;
import com.idukbaduk.metoo9dan.payments.kakaopay.KakaoReadyResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentsController {

    private final GameService gameService;
    private final GameFilesService gameFilesService;
    private final KakaoPayService kakaoPayService;

    //게임컨텐츠 목록조회
    @GetMapping("/list")
    public String gameList(Model model, @RequestParam(value = "page", defaultValue = "0") int page, GameContents gameContents) {
/*        Page<GameContents> gamePage = this.gameService.getList(page);

        // 게임컨텐츠에 대한 파일 정보를 가져와서 모델에 추가
        for (GameContents gamecon : gamePage.getContent()) {
            List<GameContentFiles> gameContentFilesList = gameFilesService.getGameFilesByGameContentNo(gamecon.getGameContentNo());
            gamecon.setGameContentFilesList(gameContentFilesList);
        }

        //3.Model
        model.addAttribute("gameContents", gameContents);
        model.addAttribute("gamePage", gamePage);*/
        return "payments/gameList";
    }

    //결제하기 폼
    @GetMapping("/ptest")
    public String paymentsform(/*KakaopayDTO kakaopayDTO, Model model, */HttpSession session) {
       // model.addAttribute("KakaopayDTO",kakaopayDTO);
        return "payments/ptest";
    }

    @PostMapping("/kakaopay")
    @ResponseBody
    public String paymentsformTEST(Model model) {

        try {
            URL kakaoURl = new URL("https://kapi.kakao.com/v1/payment/ready");
            HttpURLConnection connection = (HttpURLConnection) kakaoURl.openConnection();   //서버연결
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "KakaoAK 952a7b3082d015dcec08556a07b94b78");  // kakao- admin key
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            connection.setDoOutput(true);       //setDoOutput false가 기본이라 설정이 필요. (서버에 전해줄게 있는지? true)
            String information = "cid=TC0ONETIME&partner_order_id=partner_order_id&partner_user_id=partner_user_id&partner_user_id=partner_user_id&item_name=초코파이&quantity=1&total_amount=2200&vat_amount=200&tax_free_amount=0&approval_url=http://localhost/payments/success&fail_url=http://localhost/payments/payments/fail&cancel_url=http://localhost/payments/cancel";
            OutputStream outputStream = connection.getOutputStream();       //값을 준다.
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);   //데이터를 전달한다.
            dataOutputStream.writeBytes(information); // information의 값을 Byte형 변환 해줌
            dataOutputStream.close();   //가지고 있는걸 비우면서 전달 하고 닫아준다.

            int result = connection.getResponseCode(); //결과값
            InputStream getInput;        // 받을 준비.

            // 결과값이 200이면 통신 ok
            if (result == 200) {
                getInput = connection.getInputStream();        //결과값을 받는다.
            } else {
                getInput = connection.getErrorStream();      //에러를 받는다.
            }
            InputStreamReader inputStreamReader = new InputStreamReader(getInput);  //받아온 값을 읽는다.
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader); // 받아온 byte값을 문자열로 형변환
            System.out.println("bufferedReader :" + bufferedReader);
            return bufferedReader.readLine();   //문자열로 형변환을 해주고 찍어낸 후 비워낸다.

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /*   @PostMapping("/kakaopay")
    @ResponseBody
    public String paymentsformTEST() {

        try {
            URL kakaoURl = new URL("https://kapi.kakao.com/v1/payment/ready");
            HttpURLConnection connection = (HttpURLConnection) kakaoURl.openConnection();   //서버연결
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "KakaoAK 952a7b3082d015dcec08556a07b94b78");  // kakao- admin key
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            connection.setDoOutput(true);       //setDoOutput false가 기본이라 설정이 필요. (서버에 전해줄게 있는지? true)
            String information = "cid=TC0ONETIME&partner_order_id=partner_order_id&partner_user_id=partner_user_id&partner_user_id=partner_user_id&item_name=초코파이&quantity=1&total_amount=2200&vat_amount=200&tax_free_amount=0&approval_url=http://localhost/payments/ok&fail_url=http://localhost/payments/payments/fail&cancel_url=http://localhost/payments/cancel";
            OutputStream outputStream = connection.getOutputStream();       //값을 준다.
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);   //데이터를 전달한다.
            dataOutputStream.writeBytes(information); // information의 값을 Byte형 변환 해줌
            dataOutputStream.close();   //가지고 있는걸 비우면서 전달 하고 닫아준다.

            int result = connection.getResponseCode(); //결과값
            InputStream getInput;        // 받을 준비.

            // 결과값이 200이면 통신 ok
            if (result == 200) {
                getInput = connection.getInputStream();        //결과값을 받는다.
            } else {
                getInput = connection.getErrorStream();      //에러를 받는다.
            }
            InputStreamReader inputStreamReader = new InputStreamReader(getInput);  //받아온 값을 읽는다.
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader); // 받아온 byte값을 문자열로 형변환
            System.out.println("bufferedReader :" + bufferedReader);
            return bufferedReader.readLine();   //문자열로 형변환을 해주고 찍어낸 후 비워낸다.

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -----------------------------


    /**
     * 결제요청
     */
   /* @PostMapping("/ready")
    public ResponseEntity readyToKakaoPay() {

        return kakaoPayService.kakaoPayReady();
    }
*/



    //----------------------

    @GetMapping("/success")
    public String afterPayRequest(@RequestParam("pg_token") String pgToken, Model model) {
        System.out.println("afterPayRequest? " + pgToken);
        KakaoApproveResponse kakaoApprove = kakaoPayService.approveResponse(pgToken);

        // 여기에서 game/list.html로 리다이렉트
        return "redirect:/game/list";
    }


    //결제 취소 할때 보여주는 페이지
    @GetMapping("/cancel")
    public String paymentscancel() {
        return "payments/cancel";
    }

    //결제실패 할때 보여주는 페이지
    @GetMapping("/fail")
    public String paymentsfail() {
        return "payments/fail";
    }

    /*// 결제 이후
    @PostMapping("/success")
    @ResponseBody
    public String paymentsformok() {

        try {
            URL kakaoURl = new URL("https://kapi.kakao.com/v1/payment/approve");
            HttpURLConnection connection = (HttpURLConnection) kakaoURl.openConnection();   //서버연결
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "KakaoAK 952a7b3082d015dcec08556a07b94b78");  // kakao- admin key
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            connection.setDoOutput(true);       //setDoOutput false가 기본이라 설정이 필요. (서버에 전해줄게 있는지? true)
            String information = "cid=TC0ONETIME&tid=T1234567890123456789&partner_order_id=partner_order_id&partner_user_id=partner_user_id&pg_token=xxxxxxxxxxxxxxxxxxxx";
            OutputStream outputStream = connection.getOutputStream();       //값을 준다.
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);   //데이터를 전달한다.
            dataOutputStream.writeBytes(information); // information의 값을 Byte형 변환 해줌
            dataOutputStream.close();   //가지고 있는걸 비우면서 전달 하고 닫아준다.

            int result = connection.getResponseCode(); //결과값
            InputStream getInput;        // 받을 준비.

            // 결과값이 200이면 통신 ok
            if (result == 200) {
                getInput = connection.getInputStream();        //결과값을 받는다.
            } else {
                getInput = connection.getErrorStream();      //에러를 받는다.
            }
            InputStreamReader inputStreamReader = new InputStreamReader(getInput);  //받아온 값을 읽는다.
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader); // 받아온 byte값을 문자열로 형변환
            System.out.println("bufferedReader :" + bufferedReader);
          //  return bufferedReader.readLine();   //문자열로 형변환을 해주고 찍어낸 후 비워낸다.
            String s = bufferedReader.readLine();//문자열로 형변환을 해주고 찍어낸 후 비워낸다.
            System.out.println("s?"+ s);
            return "payments/success";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

*/

    //결제하기 요청이 들어오면
  /*  @PostMapping("/kakaopay")
    public ResponseEntity<String> addpayments() throws JsonProcessingException {

        // Create a JSON response object
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("tid", "T1234567890123456789");
        jsonResponse.put("next_redirect_app_url", "https://mockup-pg-web.kakao.com/v1/xxxxxxxxxx/aInfo");
        // Add other fields as needed

        // Convert the response object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(jsonResponse);

        // Return the JSON response
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        return new ResponseEntity<>(json, headers, HttpStatus.OK);
    }*/

}
