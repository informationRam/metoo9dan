package com.idukbaduk.metoo9dan.payments.controller;

import com.idukbaduk.metoo9dan.common.entity.*;
import com.idukbaduk.metoo9dan.education.service.EducationService;
import com.idukbaduk.metoo9dan.game.service.GameFilesService;
import com.idukbaduk.metoo9dan.game.service.GameService;
import com.idukbaduk.metoo9dan.member.service.MemberService;
import com.idukbaduk.metoo9dan.member.service.MemberServiceImpl;
import com.idukbaduk.metoo9dan.payments.kakaopay.KakaoApproveResponse;
import com.idukbaduk.metoo9dan.payments.kakaopay.KakaoPayService;
import com.idukbaduk.metoo9dan.payments.kakaopay.KakaoReadyResponse;
import com.idukbaduk.metoo9dan.payments.service.PaymentsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentsController {

    private final GameService gameService;
    private final EducationService educationService;
    private final KakaoPayService kakaoPayService;
    private final PaymentsService paymentsService;
    private final MemberService memberService;
    private final MemberServiceImpl memberServicesimpl;


    // 결제하기 폼
    @PostMapping("/paymentsform")
    public String paymentsform(@RequestParam("gameContentNos") List<Integer> gameContentNos, Model model, HttpSession session) {

        if(!gameContentNos.isEmpty()){
            System.out.println("존재함: "+gameContentNos);

        }


        if (gameContentNos != null && !gameContentNos.isEmpty()) {
            List<GameContents> selectedGameContents = new ArrayList<>();

            int totalSalePrice = 0;

            for (int gameno : gameContentNos) {
                System.out.println("gameno?"+gameno);
                GameContents gameContents = gameService.getGameContents(gameno);
                selectedGameContents.add(gameContents);
                // salePrice의 합계 계산
                totalSalePrice += (gameContents.getSalePrice());
                System.out.println("totalSalePrice?" +totalSalePrice);
            }

            // 세션에 선택한 게임 컨텐츠와 총 판매 가격 저장
            session.setAttribute("selectedGameContents", selectedGameContents);
            session.setAttribute("totalSalePrice", totalSalePrice);

        }
        return "payments/paymentsform";
    }



    /* @PostMapping("/paymentsform")
    public String paymentsform(@RequestParam(value = "gameContentNo", required = false) List<Integer> gameContentNo,  @RequestParam("gameContentNos") List<Integer> gameContentNos,Model model, HttpSession session) {

      if(!gameContentNos.isEmpty()){
          System.out.println("존재함");
      }


        if (gameContentNo != null && !gameContentNo.isEmpty()) {
            List<GameContents> selectedGameContents = new ArrayList<>();

            int totalSalePrice = 0;

            for (int gameno : gameContentNo) {
                GameContents gameContents = gameService.getGameContents(gameno);
                selectedGameContents.add(gameContents);
                // salePrice의 합계 계산
                totalSalePrice += (gameContents.getSalePrice());
                System.out.println("totalSalePrice?" +totalSalePrice);
            }

            // 세션에 선택한 게임 컨텐츠와 총 판매 가격 저장
            session.setAttribute("selectedGameContents", selectedGameContents);
            session.setAttribute("totalSalePrice", totalSalePrice);

        }
        return "payments/paymentsform";
    }

*/
    // 결제하기
    @PostMapping("/payments")
    @Transactional
    public String processPayment(@RequestParam(value = "paymentMethod") String paymentMethod,HttpSession session,Principal principal) {

        List<GameContents> selectedGameContents = (List<GameContents>) session.getAttribute("selectedGameContents");
        System.out.println("selectedGameContents?" + selectedGameContents);
        int totalSalePrice = (int) session.getAttribute("totalSalePrice");
        System.out.println("totalSalePrice:?" +totalSalePrice);
        Member member = memberService.getUser(principal.getName());


        // paymentMethod 변수에 선택한 결제 방법이 무통장이면
        if(paymentMethod.equals("deposit")){
            paymentsService.save(selectedGameContents,member,paymentMethod);
            memberServicesimpl.userUpdate(member);
        }else if(paymentMethod.equals("account")){
            paymentsService.save(selectedGameContents,member,paymentMethod);
            memberServicesimpl.userUpdate(member);
        }else {
            return "payments/paymentsform";
        }
        return "redirect:/payments/list";
    }


    //구매 목록조회
    @GetMapping("/list")
    public String paymentsList(Model model, @RequestParam(value = "page", defaultValue = "0") int page, Payments payments,Principal principal) {

        Member member = memberService.getUser(principal.getName());
        Page<Payments> paymentsPage = paymentsService.paymentsList(member.getMemberNo(), page);

        // Create a list to store the associated GameContents
        List<GameContents> gameContentsList = new ArrayList<>();

        for (Payments payment : paymentsPage) {
            GameContents gameContentsForPayment = paymentsService.getGameContentsForPayment(payment);
            gameContentsList.add(gameContentsForPayment);
        }

        for (Payments payment : paymentsPage) {
            GameContents gameContentsForPayment = paymentsService.getGameContentsForPayment(payment);

            // Fetch and add associated EducationalResources
            List<EducationalResources> educationalResources = educationService.getEducationalResourcesForGameContents(gameContentsForPayment);
            gameContentsForPayment.setEducationalResourcesList(educationalResources);

            gameContentsList.add(gameContentsForPayment);
        }


            int currentPage = paymentsPage.getPageable().getPageNumber();
        int totalPages = paymentsPage.getTotalPages();
        int pageRange = 5; // 한 번에 보여줄 페이지 범위

        int startPage = Math.max(0, currentPage - pageRange / 2);
        int endPage = startPage + pageRange - 1;
        if (endPage >= totalPages) {
            endPage = totalPages - 1;
            startPage = Math.max(0, endPage - pageRange + 1);
        }

// 페이지 번호에 1을 더해줍니다.
        startPage += 1;
        endPage += 1;

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("paymentsPage", paymentsPage);
        model.addAttribute("gameContentsList", gameContentsList);

        return "payments/list";
    }

    //상세조회
    @GetMapping("/detail/{paymentNo}")
    public String paymentDetail(@PathVariable Integer paymentNo,Model model, @RequestParam(value = "page", defaultValue = "0") int page, Payments payments,Principal principal) {

        Member member = memberService.getUser(principal.getName());

        Payments payment = paymentsService.getPayment(paymentNo);
        GameContents gameContents = payment.getGameContents();
        List<EducationalResources> educationTogameno = gameContents.getEducationalResourcesList();

        model.addAttribute("payment", payment);
        model.addAttribute("gameContents", gameContents);
        model.addAttribute("education", educationTogameno);

        return "payments/detail";
    }


    // 결제 성공시
    @GetMapping("/success")
    public String afterPayRequest(@RequestParam("pg_token") String pgToken, Model model, Principal principal, HttpSession session, HttpServletRequest request) {

        List<GameContents> selectedGameContents = (List<GameContents>) session.getAttribute("selectedGameContents");
        int totalSalePrice = (int) session.getAttribute("totalSalePrice");

        Member member = memberService.getUser(principal.getName());
        String pay = "pay";
        KakaoApproveResponse kakaoApprove = kakaoPayService.approveResponse(pgToken,member,selectedGameContents,pay);

        // 세션을 종료 (무효화)
        session = request.getSession();
        session.invalidate();

        // 구매목록으로 redirect
        return "redirect:/payments/list";
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

}
