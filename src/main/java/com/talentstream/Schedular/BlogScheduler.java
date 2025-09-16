package com.talentstream.Schedular;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.talentstream.service.BlogService;


@Component
@EnableScheduling
public class BlogScheduler {

    private final BlogService blogService;

    public BlogScheduler(BlogService blogService) {
        this.blogService = blogService;
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void fetchTechNewsDailyAt10AM() {
        blogService.fetchAndSaveTechNews();
    }

}
