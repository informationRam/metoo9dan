package com.idukbaduk.metoo9dan.admin.repository;

import com.idukbaduk.metoo9dan.common.entity.EducatorInfo;
import com.idukbaduk.metoo9dan.common.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsRepository extends JpaRepository<Payments, Integer> {

    //MemNo있는지 찾기 -> 유무료 회원 구분
    boolean existsByMemberMemberNo(Integer memberNo);
}
