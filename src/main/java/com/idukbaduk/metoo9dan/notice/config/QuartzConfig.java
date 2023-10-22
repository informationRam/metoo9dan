package com.idukbaduk.metoo9dan.notice.config;

import com.idukbaduk.metoo9dan.notice.service.UpdatePosting;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail myJobDetail() {
        return JobBuilder.newJob(UpdatePosting.class)
                .withIdentity("updatePosting")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger myTrigger(JobDetail job) {
        return TriggerBuilder.newTrigger()
                .forJob(job)
                .withIdentity("myTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 43 12 * * ?")) // Example cron expression
                .build();
    }
}
