package com.kapiki_akapikebula.app.scheduler;

import com.kapiki_akapikebula.app.model.PriceHistory;
import com.kapiki_akapikebula.app.model.ShopProducts;
import com.kapiki_akapikebula.app.repository.PriceHistoryRepository;
import com.kapiki_akapikebula.app.repository.ShopProductsRepository;
import com.kapiki_akapikebula.app.service.ScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Scheduled(fixedDelay = 43200000)
    public void runScrapingJobs() {
        log.info("Starting scheduled scraping job...");


        List<ShopProducts> listings = shopProductsRepository.findAll();

        for (ShopProducts prod : listings) {
            try {

                BigDecimal newPrice = scraperService.scrapeLatestPrice(prod.getProductUrl(), prod.getPrice());
                if (newPrice != null && prod.getPrice().compareTo(newPrice) != 0) {
                    log.info("Price changed for URL {}: Old: {} -> New: {}",
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
                log.error("Scraping job interrupted", ie);
                break;
            } catch (Exception e) {
                log.error("Failed to scrape and update URL: {}. Error: {}",
                        prod.getProductUrl(), e.getMessage());
            }
        }

        log.info("Finished scheduled scraping job.");
    }
}
