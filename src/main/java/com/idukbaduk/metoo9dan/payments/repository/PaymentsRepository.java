package com.idukbaduk.metoo9dan.payments.repository;

import com.idukbaduk.metoo9dan.common.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsRepository extends JpaRepository<Payments, Integer> {

    //MemNo있는지 찾기 : 유무료 회원 구분 YJ 추가
    boolean existsByMemberMemberNo(Integer memberNo);

    //회원 결제횟수 찾기 : YJ 추가
    int countPaymentNoByMemberMemberNo(Integer memberNo);

}

