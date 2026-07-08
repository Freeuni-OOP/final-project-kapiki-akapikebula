package com.kapiki_akapikebula.app.scheduler;

import com.kapiki_akapikebula.app.model.PriceHistory;
import com.kapiki_akapikebula.app.model.ShopProducts;
import com.kapiki_akapikebula.app.repository.PriceHistoryRepository;
import com.kapiki_akapikebula.app.repository.ShopProductsRepository;
import com.kapiki_akapikebula.app.seedrunner.SeedRunner;
import com.kapiki_akapikebula.app.service.ScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledScraperRunner {

    private final ShopProductsRepository shopProductsRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final ScraperService scraperService;
    private final SeedRunner seedRunner;          // inject the seed runner

    // Runs every 12 hours — discovers new products AND updates existing prices
    @Scheduled(fixedDelay = 43200000)
    public void runScrapingJobs() {

        // --- Job 1: Discover and insert new products ---
        log.info("Starting product discovery...");
        seedRunner.run();
        log.info("Product discovery complete.");

        // --- Job 2: Update prices of products already in DB ---
        log.info("Starting price update job...");

        List<ShopProducts> listings = shopProductsRepository.findAll();

        for (ShopProducts prod : listings) {
            try {
                BigDecimal newPrice = scraperService.scrapeLatestPrice(
                        prod.getProductUrl(), prod.getPrice());

                if (newPrice != null && prod.getPrice().compareTo(newPrice) != 0) {
                    log.info("Price changed for {}: {} -> {}",
                            prod.getProductUrl(), prod.getPrice(), newPrice);

                    PriceHistory history = new PriceHistory();
                    history.setProduct(prod.getProduct());
                    history.setPrice(newPrice);
                    priceHistoryRepository.save(history);

                    prod.setPrice(newPrice);
                    prod.setLastUpdated(LocalDateTime.now());
                    shopProductsRepository.save(prod);
                }

                Thread.sleep(1000);

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("Price update job interrupted", ie);
                break;
            } catch (Exception e) {
                log.error("Failed to update price for URL: {}. Error: {}",
                        prod.getProductUrl(), e.getMessage());
            }
        }

        log.info("Price update job complete.");
    }
}