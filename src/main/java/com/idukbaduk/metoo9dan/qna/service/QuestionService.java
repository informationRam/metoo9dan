package com.idukbaduk.metoo9dan.qna.service;

import com.idukbaduk.metoo9dan.common.entity.QnaQuestions;
import com.idukbaduk.metoo9dan.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    //목록조회
    public Page<QnaQuestions> getList(int pageNo) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("questionNo"));
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(sorts));
        return questionRepository.findAll(pageable);
    }


}
