package com.idukbaduk.metoo9dan.notice.service;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class UpdatePosting implements Job {

    private final NoticeService noticeService;
    Logger logger = LoggerFactory.getLogger(UpdatePosting.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("updateStatusForPostDateEqualToday()실행");
        noticeService.updateStatusForPostDateEqualToday();
    }
}
