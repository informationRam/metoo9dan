package com.idukbaduk.metoo9dan.admin.service;

import com.idukbaduk.metoo9dan.payments.repository.PaymentsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class MemPaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final ModelMapper modelMapper;

    public int getPaymentCountByMemberNo(Integer memberNo) {
        int paymentCount = paymentsRepository.countPaymentNoByMemberMemberNo(memberNo);
        return paymentCount;
    }
}
