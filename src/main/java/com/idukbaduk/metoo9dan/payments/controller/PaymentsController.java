package com.idukbaduk.metoo9dan.payments.controller;

import com.idukbaduk.metoo9dan.common.entity.*;
import com.idukbaduk.metoo9dan.education.service.EducationService;
import com.idukbaduk.metoo9dan.education.service.ResourcesFilesService;
import com.idukbaduk.metoo9dan.game.service.GameService;
import com.idukbaduk.metoo9dan.member.service.MemberService;
import com.idukbaduk.metoo9dan.member.service.MemberServiceImpl;
import com.idukbaduk.metoo9dan.payments.kakaopay.KakaoApproveResponse;
import com.idukbaduk.metoo9dan.payments.kakaopay.KakaoPayService;
import com.idukbaduk.metoo9dan.payments.service.PaymentsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final ResourcesFilesService resourcesFilesService;


    // 결제하기 폼
    //@RequestMapping(value = "/paymentsform", method = {RequestMethod.GET, RequestMethod.POST})
    @PostMapping("/paymentsform")
    @PreAuthorize("hasAuthority('EDUCATOR') or hasAuthority('NORMAL') or hasAuthority('ADMIN')")
    public String paymentsform(@RequestParam(name = "gameContentNos") String gameContentNos, Model model, HttpSession session,Principal principal) {

        Member user = memberService.getUser(principal.getName());
        session.setAttribute("user",user);

        try {
            // gameContentNos 값을 JSON 배열로 파싱
            JSONArray jsonArray = new JSONArray(gameContentNos);

            // JSON 배열을 순회하며 각 항목을 int로 변환
            List<Integer> gameContentNoList = new ArrayList<>();
            List<GameContents> gameContents = new ArrayList<>();
            Double total = 0.0;

            for (int i = 0; i < jsonArray.length(); i++) {
                int gameContentNo = jsonArray.getInt(i);
                gameContentNoList.add(gameContentNo);

                gameContents.add(gameService.getGameContents(gameContentNo));
                Double salePrice = gameContents.get(i).getSalePrice();
                total += salePrice;

            }

            int totalSalePrice = total.intValue();

            session.setAttribute("totalSalePrice", totalSalePrice);
            session.setAttribute("selectedGameContents", gameContents);

            // 리스트 형태로 컨트롤러에서 사용할 수 있음
            model.addAttribute("gameContentNos", gameContentNoList);
            model.addAttribute("gameContents", gameContents);
            model.addAttribute("totalSalePrice", totalSalePrice);

            return "payments/paymentsform";
        } catch (JSONException e) {
            // JSON 파싱 오류 처리
            e.printStackTrace();

            // ------! 오류 메시지를 반환하거나 오류 페이지로 리다이렉트할 수 있습니다.
            return "redirect:error"; //
        }
    }

    // 결제하기
    @PostMapping("/payments")
    @Transactional
    public String processPayment(@RequestParam(value = "paymentMethod") String paymentMethod,HttpSession session,
                                 Principal principal) {

        List<GameContents> selectedGameContents = (List<GameContents>) session.getAttribute("selectedGameContents");
        int totalSalePrice = (int) session.getAttribute("totalSalePrice");
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
    @PreAuthorize("hasAuthority('EDUCATOR') or hasAuthority('NORMAL') or hasAuthority('ADMIN')")
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
    @GetMapping("/detail/{gameContentNo}")
    public String paymentDetail(@PathVariable Integer gameContentNo,Model model, Payments payments,Principal principal) {

            GameContents gameContents = gameService.getGameContents(gameContentNo);

            List<EducationalResources> educationTogameno = educationService.getEducation_togameno(gameContentNo);
        // 교육자료에 대한 파일 정보를 가져와서 모델에 추가
            for (EducationalResources education : educationTogameno) {
                ResourcesFiles resourcesFilesList = resourcesFilesService.getFile(education.getResourceNo());
                education.setResourcesFilesList(resourcesFilesList);
            }

            //게임컨텐츠값으로 선택된 EducationalResources정보를 가져온다.
            List<EducationalResources> selectEducation = educationService.getEducation_togameno(gameContents.getGameContentNo());

            List<Integer> resourcesNo = new ArrayList<>();
            for(EducationalResources educationalResources1 : selectEducation){
                resourcesNo.add(educationalResources1.getResourceNo());
            }

            model.addAttribute("resourcesNo", resourcesNo);
            model.addAttribute("selectEducation", selectEducation);
            model.addAttribute("gameContents", gameContents);

        return "payments/detail";
    }


    // 결제 성공시
    @GetMapping("/success")
    public String afterPayRequest(@RequestParam("pg_token") String pgToken, Model model, Principal principal, HttpSession session, HttpServletRequest request) {

        List<GameContents> selectedGameContents = (List<GameContents>) session.getAttribute("selectedGameContents");
        int totalSalePrice = (int) session.getAttribute("totalSalePrice");

        Member member = memberService.getUser(principal.getName());
        String pay = "pay";
        KakaoApproveResponse kakaoApprove = kakaoPayService.approveResponse(pgToken,member,selectedGameContents,pay,member.getMemberId());

        // 세션을 종료.
        session.removeAttribute("selectedGameContents");
        session.removeAttribute("totalSalePrice");

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

    // 월별,월별 조회 기능 구현 및 페이지네이션 처리
    @GetMapping("/showPayments")
    public String showMonthlyPayments(@RequestParam(name = "startDate", defaultValue = "") String startDate,
                                      @RequestParam(name = "endDate", defaultValue = "") String endDate,
                                      @RequestParam(name = "view", defaultValue = "daily") String view,
                                      @RequestParam(name = "page", defaultValue = "0") int page, Model model) {

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        int totalTransactionCount = 0;
        int totalAmount = 0;

        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startDateTime = LocalDate.parse(startDate, formatter).atStartOfDay();
            endDateTime = LocalDate.parse(endDate, formatter).atTime(LocalTime.MAX);

        } else {
            LocalDate now = LocalDate.now();
            startDateTime = now.withDayOfMonth(1).atStartOfDay();
            endDateTime = now.withDayOfMonth(now.lengthOfMonth()).atTime(LocalTime.MAX);
        }

        Page<Object[]> paymentsPage;

        if ("month".equals(view)) {
            paymentsPage = paymentsService.getMonthlyTotalAmounts(startDateTime, endDateTime, page);

            // 월별 합계를 계산
            List<Object[]> monthlySummaries = paymentsService.getMonthlySummaries(startDateTime, endDateTime);
            totalTransactionCount = 0;
            totalAmount = 0; // 매출을 정수로 저장
            for (Object[] summary : monthlySummaries) {
                totalTransactionCount += ((Long) summary[1]).intValue();
                totalAmount += ((Double) summary[2]).intValue();
            }

        } else if("daily".equals(view)){
            paymentsPage = paymentsService.getDailyPayments(startDateTime, endDateTime, page);

            // 일별 합계를 계산
            List<Object[]> monthlySummaries = paymentsService.getDailySummaries(startDateTime, endDateTime);
            totalTransactionCount = 0;
            totalAmount = 0; // 매출을 정수로 저장
            for (Object[] summary : monthlySummaries) {
                totalTransactionCount += ((Long) summary[1]).intValue();
                totalAmount += ((Double) summary[2]).intValue();
            }

        }else {
            paymentsPage = paymentsService.getDailyPayments(startDateTime, endDateTime, page);

            // 일별 합계를 계산
            List<Object[]> monthlySummaries = paymentsService.getDailySummaries(startDateTime, endDateTime);
            totalTransactionCount = 0;
            totalAmount = 0; // 매출을 정수로 저장
            for (Object[] summary : monthlySummaries) {
                totalTransactionCount += ((Long) summary[1]).intValue();
                totalAmount += ((Double) summary[2]).intValue();
                }
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

        //3.Model
            model.addAttribute("paymentsPage", paymentsPage);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("totalTransactionCount", totalTransactionCount);
            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("view", view);

        return "payments/showPayments";
    }
}
