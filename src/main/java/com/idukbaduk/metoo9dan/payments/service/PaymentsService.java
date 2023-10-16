package com.idukbaduk.metoo9dan.payments.service;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.Payments;
import com.idukbaduk.metoo9dan.payments.reprository.PaymentsRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private EntityManager entityManager;

    // 구매한 게임컨텐츠에 대한 정보를 가져온다.
    public List<Payments> paymentsList(Integer memberNo){

        List<Payments> paymentsList = paymentsRepository.findByMemberMemberNo(memberNo);
        return paymentsList;

    }


// OrderNumber의 가장 큰 값을 가져온다.

    // OrderNumber의 가장 큰 값을 가져온다.
    public int generateOrderNumber() {
        Integer maxOrderNumber = paymentsRepository.findMaxOrderNumber();
        if (maxOrderNumber == null) {
            // 주문 번호를 생성할 데이터가 없다면 1로 시작
            return 1;
        } else {
            // 가장 큰 주문 번호를 찾아서 1을 더함
            int newOrderNumber = maxOrderNumber + 1;
            return newOrderNumber;
        }
    }


    //결제하기
    public void save(List<GameContents> selectedGameContents, Member member, String paymentMethod){
        int orderNumber =  generateOrderNumber();

        for(GameContents gameContents: selectedGameContents){
            Payments payments = new Payments();
            payments.setOrderNumber(orderNumber);
            payments.setContact(member.getTel());
            payments.setMethod(paymentMethod);
            payments.setPaymentDate(LocalDateTime.now());
            payments.setStatus("complete");     //complete,waiting,refund
            payments.setAmount(gameContents.getSalePrice());
            payments.setDepositorName(member.getName());
            payments.setGameContents(gameContents);
            payments.setMember(member);
            paymentsRepository.save(payments);
        }
    }




   /* //결제 내용 저장하기 (DB)
    public void kakaosave(KakaoApproveResponse kakaoApprove){
*//*
        Payments payments = new Payments();
        payments.setOrderNumber();
        payments.setContact();
        payments.set;


        kakaoApprove.set
*//*


        paymentsRepository.save(payments);
    }
*/


}
