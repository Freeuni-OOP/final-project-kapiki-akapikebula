package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.model.*;
import com.kapiki_akapikebula.app.repository.*;
import com.kapiki_akapikebula.app.scraper.StoreListing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScraperIngestionService {

    private final ProductRepository productRepository;
    private final ShopProductsRepository shopProductsRepository;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;

    // The ID we inserted in the migration for "Uncategorized"
    private static final long DEFAULT_CATEGORY_ID = 1L;

    @Transactional
    public void ingest(List<StoreListing> listings, String shopName) {

        // Look up the shop once — fails loud if the shop isn't in the DB yet
        Shop shop = shopRepository.findByName(shopName)
                .orElseThrow(() -> new RuntimeException(
                        "Shop not found: " + shopName + ". Check your V9 migration ran correctly."));

        // We'll attach every scraped product to the default category for now
        Category defaultCategory = categoryRepository.findById(DEFAULT_CATEGORY_ID)
                .orElseThrow(() -> new RuntimeException(
                        "Default category not found. Check your V9 migration ran correctly."));

        int saved = 0;
        int skipped = 0;

        for (StoreListing listing : listings) {
            try {
                processSingleListing(listing, shop, defaultCategory);
                saved++;
            } catch (Exception e) {
                // One bad listing shouldn't stop the whole batch
                System.out.println("Skipped [" + listing.getProductName() + "]: " + e.getMessage());
                skipped++;
            }
        }

        System.out.printf("Ingestion complete for %s — saved: %d, skipped: %d%n",
                shopName, saved, skipped);
    }

    private void processSingleListing(StoreListing listing, Shop shop, Category defaultCategory) {

        // --- Step 1: Build match key ---
        // Lowercase + trim so "Samsung Galaxy S25" and "samsung galaxy s25" are the same product
        String matchKey = listing.getProductName().toLowerCase().trim();

        // --- Step 2: Find existing product or create a new one ---
        Product product = productRepository.findByMatchKey(matchKey)
                .orElseGet(() -> createProduct(listing, matchKey, defaultCategory));

        // --- Step 3: Find existing shop listing for this product+shop, or create one ---
        // This prevents duplicate rows if we run the scraper twice
        ShopProducts shopProduct = shopProductsRepository
                .findByProductIdAndShopId(product.getId(), shop.getId())
                .orElseGet(() -> {
                    ShopProducts sp = new ShopProducts();
                    sp.setProduct(product);
                    sp.setShop(shop);
                    return sp;
                });

        // Update fields that change on every scrape
        shopProduct.setPrice(BigDecimal.valueOf(listing.getPrice()));
        shopProduct.setStockStatus(listing.isInStock() ? "IN_STOCK" : "OUT_OF_STOCK");
        shopProduct.setProductUrl(listing.getRoute());
        shopProduct.setLastUpdated(LocalDateTime.now());

        shopProductsRepository.save(shopProduct);
    }

    private Product createProduct(StoreListing listing, String matchKey, Category defaultCategory) {
        Product product = new Product();
        product.setName(listing.getProductName());
        product.setMatchKey(matchKey);
        product.setImageUrl(listing.getImageUrl());
        product.setCategory(defaultCategory);
        // Brand can be set later once you have attribute mapping
        return productRepository.save(product);
    }
}
