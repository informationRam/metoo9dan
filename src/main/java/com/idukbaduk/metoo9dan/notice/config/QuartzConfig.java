package com.idukbaduk.metoo9dan.notice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    /*@Bean
    public JobDetail myJobDetail() {
        return JobBuilder.newJob(MyJob.class)
                .withIdentity("myJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger myTrigger(JobDetail job) {
        return TriggerBuilder.newTrigger()
                .forJob(job)
                .withIdentity("myTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")) // Example cron expression
                .build();
    }*/
}
