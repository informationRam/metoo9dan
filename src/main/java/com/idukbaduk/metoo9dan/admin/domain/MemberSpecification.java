package com.idukbaduk.metoo9dan.admin.domain;

import com.idukbaduk.metoo9dan.admin.exception.InvalidSearchCriteriaException;
import com.idukbaduk.metoo9dan.common.entity.Member;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MemberSpecification {
    public static Specification<Member> searchMembers(String startDate, String endDate, String memberType, String membershipStatus, String searchCriteria, String searchKeyword) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // startDate와 endDate가 빈 문자열이 아닐 때만 조건 추가
            if (!isEmpty(startDate) && !isEmpty(endDate)) {
                predicates.add(builder.between(root.get("joinDate"), startDate, endDate));
            }

            if (!isEmpty(memberType)) {
                predicates.add(builder.equal(root.get("role"), memberType));
            }

            if (!isEmpty(membershipStatus)) {
                predicates.add(builder.equal(root.get("membershipStatus"), membershipStatus));
            }

            //검색조건(휴대폰,ID,이름)에서 검색
            if (!isEmpty(searchCriteria) && !isEmpty(searchKeyword)) {
                Expression<String> searchField = getSearchField(root, searchCriteria);
                predicates.add(builder.like(searchField, "%" + searchKeyword + "%"));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // 빈 문자열 및 null 검사 도우미 메서드
    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    //검색조건 객체
    private static Expression<String> getSearchField(Root<Member> root, String searchCriteria) {
        return switch (searchCriteria) {
            case "id" -> root.get("memberId");
            case "phone" -> root.get("tel");
            case "name" -> root.get("name");
            default -> throw new InvalidSearchCriteriaException("유효하지 않은 검색 조건: " + searchCriteria);
        };
    }
}
