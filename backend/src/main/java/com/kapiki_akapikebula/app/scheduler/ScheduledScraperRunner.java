package com.kapiki_akapikebula.app.scheduler;

import com.kapiki_akapikebula.app.seedrunner.SeedRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledScraperRunner {

    private final SeedRunner seedRunner;

    @Scheduled(fixedDelay = 43200000)
    public void runScrapingJobs() {

        log.info("Starting scheduled product discovery and product update job...");
        try{
            seedRunner.run();
            log.info("Scheduled scraping job completed successfully");
        } catch(Exception e){
            log.error("Error occurred during scheduled scraping job.");
        }
    }
}