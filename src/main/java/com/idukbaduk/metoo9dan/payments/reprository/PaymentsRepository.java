package com.idukbaduk.metoo9dan.payments.reprository;

import com.idukbaduk.metoo9dan.common.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, Integer> {

    // 가장 큰 OrderNumber값 가져옴
    public Integer findMaxOrderNumber();

}
