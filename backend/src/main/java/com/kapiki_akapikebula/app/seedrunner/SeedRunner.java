package com.kapiki_akapikebula.app.seedrunner;

import com.kapiki_akapikebula.app.scraper.EEApiScraper;
import com.kapiki_akapikebula.app.scraper.ZoommerApiScraper;
import com.kapiki_akapikebula.app.service.ScraperIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeedRunner {

    private final ScraperIngestionService ingestionService;
    private final ZoommerApiScraper zoommerScraper;
    private final EEApiScraper eeScraper;

    private static final List<String> SEED_QUERIES = List.of(
            "samsung" //"apple", "laptop", "tablet", "tv",
//            "fridge", "washing machine", "headphones", "camera"
    );

    public void run() {
        log.info("Starting MULTITHREADED product discovery...");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for(String query : SEED_QUERIES){
            futures.add(CompletableFuture.runAsync(() -> {
                try{
                    log.info("Scraping Zoommer for: {}", query);
                    ingestionService.ingest(zoommerScraper.search(query), "Zoommer");
                    log.info("Zoommer finished for: {}", query);
                } catch(Exception e){
                    log.error("Zoommer failed for query [{}]: {}", query, e.getMessage());
                }
            }, executor));

            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    log.info("Scraping EE for: {}", query);
                    ingestionService.ingest(eeScraper.search(query), "EE");
                    log.info("EE finished for: {}", query);
                } catch (Exception e) {
                    log.error("EE failed for query [{}]: {}", query, e.getMessage());
                }
            }, executor));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        executor.shutdown();
        log.info("Seed run complete.");
    }

}
