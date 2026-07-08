package com.kapiki_akapikebula.app.seedrunner;

import com.kapiki_akapikebula.app.scraper.EEApiScraper;
import com.kapiki_akapikebula.app.scraper.ZoommerApiScraper;
import com.kapiki_akapikebula.app.service.ScraperIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeedRunner {

    private final ScraperIngestionService ingestionService;
    private final ZoommerApiScraper zoommerScraper;
    private final EEApiScraper eeScraper;

    private static final List<String> SEED_QUERIES = List.of(
            "samsung",
            "apple",
            "laptop",
            "tablet",
            "tv",
            "fridge",
            "washing machine",
            "headphones",
            "camera"
    );

    // Call this method to scrape fresh products into the DB
    public void run() {
        for (String query : SEED_QUERIES) {
            try {
                log.info("Scraping Zoommer for: {}", query);
                ingestionService.ingest(zoommerScraper.search(query), "Zoommer");

                log.info("Scraping EE for: {}", query);
                ingestionService.ingest(eeScraper.search(query), "EE");

            } catch (Exception e) {
                log.error("Seed query [{}] failed: {}", query, e.getMessage());
                // Continue with next query even if this one failed
            }
        }
        log.info("Seed run complete.");
    }
}