package com.idukbaduk.metoo9dan.homework.repository;

import com.idukbaduk.metoo9dan.common.entity.Payments;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HwPaymentRepository extends JpaRepository<Payments, Integer> {
    @Query(value = "SELECT DISTINCT gc.gameName " +
            "FROM Payments p " +
            "JOIN p.gameContents gc " +
            "JOIN p.member mb " +
            "WHERE mb.memberId = :memberId " +
            "AND FUNCTION('DATEDIFF', CURRENT_DATE, p.paymentDate) < gc.subscriptionDuration")
    List<String> findActiveGameContentTitlesByMemberId(@Param("memberId") String memberId);

}
