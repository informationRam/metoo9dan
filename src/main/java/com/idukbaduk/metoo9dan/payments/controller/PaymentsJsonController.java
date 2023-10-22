package com.idukbaduk.metoo9dan.payments.controller;

import com.idukbaduk.metoo9dan.payments.service.PaymentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/paymentsChart")  // 엔드포인트의 베이스 URL 설정
public class PaymentsJsonController {

    @Autowired
    private PaymentsService paymentsService;

    @GetMapping("/showPayments")
    public Map<String, Object> showPayments(@RequestParam("view") String view,
                                            @RequestParam(name = "startDate", defaultValue = "") String startDate,
                                            @RequestParam(name = "endDate", defaultValue = "") String endDate,
                                            @RequestParam(name = "page", defaultValue = "0") int page) {
        Map<String, Object> responseData = new HashMap<>();

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        int totalTransactionCount = 0;
        int totalAmount = 0;

        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startDateTime = LocalDate.parse(startDate, formatter).atStartOfDay();
            endDateTime = LocalDate.parse(endDate, formatter).atTime(LocalTime.MAX);

            System.out.println("---2.DateTimeFormatter?" + formatter);
            System.out.println("---2.startDate?" + startDateTime);
            System.out.println("---2.endDate?" + endDateTime);
        } else {
            LocalDate now = LocalDate.now();
            startDateTime = now.withDayOfMonth(1).atStartOfDay();
            endDateTime = now.withDayOfMonth(now.lengthOfMonth()).atTime(LocalTime.MAX);
        }

        Page<Object[]> paymentsPage;
        Map<String, Object> dataMap;


        if ("month".equals(view)) {
            paymentsPage = paymentsService.getMonthlyTotalAmounts(startDateTime, endDateTime, page);
            // 월별 데이터를 반환
            responseData.put("data", getMonthlyData(startDateTime, endDateTime));
        } else if ("daily".equals(view)) {
            paymentsPage = paymentsService.getDailyPayments(startDateTime, endDateTime, page);
            // 일별 데이터를 반환
            responseData.put("data", getDailyData(startDateTime, endDateTime));
        } else {
            paymentsPage = paymentsService.getDailyPayments(startDateTime, endDateTime, page);
            // 일별 데이터를 반환 (기본값)
            responseData.put("data", getDailyData(startDateTime, endDateTime));
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

        // 페이지 정보도 추가
        responseData.put("currentPage", currentPage);
        responseData.put("totalPages", totalPages);
        responseData.put("startPage", startPage);
        responseData.put("endPage", endPage);

        return responseData;
    }


    public Map<String, Object> getMonthlyData(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Object[]> monthlySummaries = paymentsService.getMonthlySummaries(startDateTime, endDateTime);

        Map<String, Object> dataMap = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Integer> salesData = new ArrayList<>();

        for (Object[] summary : monthlySummaries) {
            labels.add(summary[0].toString());
            System.out.println("summary[0].toString()?"+summary[0].toString());

            salesData.add(((Double) summary[2]).intValue());
        }

        dataMap.put("labels", labels);
        dataMap.put("salesData", salesData);

        return dataMap;
    }

    public Map<String, Object> getDailyData(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Object[]> dailySummaries = paymentsService.getFormattedDailySummaries(startDateTime, endDateTime);

        Map<String, Object> dataMap = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Integer> salesData = new ArrayList<>();

        for (Object[] summary : dailySummaries) {
            labels.add((String) summary[0]);
            salesData.add(((Double) summary[2]).intValue());
        }

        dataMap.put("labels", labels);
        dataMap.put("salesData", salesData);

        return dataMap;
    }


}
