package com.idukbaduk.metoo9dan.payments.reprository;

import com.idukbaduk.metoo9dan.common.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, Integer> {

    // 가장 큰 OrderNumber값 가져옴
    @Query("SELECT MAX(p.orderNumber) FROM Payments p")
    Integer findMaxOrderNumber();

    List<Payments> findByMemberMemberNo(Integer memberNo);


}
