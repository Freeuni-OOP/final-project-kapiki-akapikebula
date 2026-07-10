package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.model.*;
import com.kapiki_akapikebula.app.repository.*;
import com.kapiki_akapikebula.app.scraper.StoreListing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScraperIngestionService {

    private final ProductRepository productRepository;
    private final ShopProductsRepository shopProductsRepository;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;

    private static final long DEFAULT_CATEGORY_ID = 1L;

    public void ingest(List<StoreListing> listings, String shopName) {

        Shop shop = shopRepository.findByName(shopName)
                .orElseThrow(() -> new RuntimeException("Shop not found: " + shopName));

        Category defaultCategory = categoryRepository.findById(DEFAULT_CATEGORY_ID)
                .orElseThrow(() -> new RuntimeException("Default category not found"));

        int saved = 0, skipped = 0;

        for (StoreListing listing : listings) {
            try {
                processSingleListing(listing, shop, defaultCategory);
                saved++;
            } catch (Exception e) {
                System.out.println("Skipped [" + listing.getProductName() + "]: " + e.getMessage());
                skipped++;
            }
        }

        System.out.printf("Ingestion complete for %s — saved: %d, skipped: %d%n", shopName, saved, skipped);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processSingleListing(StoreListing listing, Shop shop, Category defaultCategory) {

        String rawName = listing.getProductName();

        String searchToken = MatchKeyBuilder.pickSearchToken(rawName);
        List<Product> candidates = searchToken.isBlank()
                ? List.of()
                : productRepository.findByNameContainingIgnoreCase(searchToken);

        Product product = candidates.stream()
                .filter(p -> MatchKeyBuilder.isMatch(rawName, p.getName()))
                .max(Comparator.comparingDouble(p -> MatchKeyBuilder.similarityScore(rawName, p.getName())))
                .orElseGet(() -> createProduct(listing, defaultCategory));

        ShopProducts shopProduct = shopProductsRepository
                .findByProductIdAndShopId(product.getId(), shop.getId())
                .orElseGet(() -> {
                    ShopProducts sp = new ShopProducts();
                    sp.setProduct(product);
                    sp.setShop(shop);
                    return sp;
                });

        shopProduct.setPrice(BigDecimal.valueOf(listing.getPrice()));
        shopProduct.setStockStatus(listing.isInStock() ? "IN_STOCK" : "OUT_OF_STOCK");
        shopProduct.setProductUrl(listing.getRoute());
        shopProduct.setLastUpdated(LocalDateTime.now());

        shopProductsRepository.save(shopProduct);
    }

    private Product createProduct(StoreListing listing, Category defaultCategory) {
        Product product = new Product();
        product.setName(listing.getProductName());
        product.setMatchKey(listing.getProductName().toLowerCase().trim()); // kept for reference only, no longer used for lookups
        product.setImageUrl(listing.getImageUrl());
        product.setCategory(defaultCategory);
        return productRepository.save(product);
    }
}