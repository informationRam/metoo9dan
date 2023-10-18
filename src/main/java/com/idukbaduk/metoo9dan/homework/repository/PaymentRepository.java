package com.idukbaduk.metoo9dan.homework.repository;

import com.idukbaduk.metoo9dan.common.entity.Payments;
import com.idukbaduk.metoo9dan.common.entity.StudyGroups;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payments, Integer> {
    @Query("SELECT DISTINCT gc.gameName " +
            "FROM Payments p " +
            "JOIN p.gameContents gc " +
            "JOIN p.member mb " +
            "WHERE mb.memberId = :memberId " +
            "AND FUNCTION('DATEDIFF', CURRENT_DATE, p.paymentDate) < gc.subscriptionDuration")
    List<String> findActiveGameContentTitlesByMemberId(@Param("memberId") String memberId);

}
