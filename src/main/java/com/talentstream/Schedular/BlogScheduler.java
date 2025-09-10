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

//    // Every Monday & Thursday at 9 AM
//    @Scheduled(cron = "0 0 9 ? * MON,THU")
    
 // Runs every 2 minutes
    @Scheduled(fixedRate = 120000) // 2 min = 120,000 ms
    public void fetchTechNewsEveryTwoMinutes() {
        blogService.fetchAndSaveTechNews();
    }

}
