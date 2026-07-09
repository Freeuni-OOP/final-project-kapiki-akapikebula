package com.kapiki_akapikebula.app.scraper;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class StoreListing {
    // Getters
    @Getter
    private String id;
    @Getter
    private String storeName;
    @Getter
    private String productName;
    @Getter
    private double price;
    @Getter
    private Double previousPrice;
    private Boolean inStock;
    @Getter
    private String imageUrl;
    @Getter
    private String route;
    // Setters for fields populated after construction
    @Getter
    @Setter
    private String matchKey;
    @Getter
    @Setter
    private Map<String, String> attributes;

    public StoreListing(String id, String storeName, String productName,
                        double price, Double previousPrice, Boolean inStock,
                        String imageUrl, String route) {
        this.id = id;
        this.storeName = storeName;
        this.productName = productName;
        this.price = price;
        this.previousPrice = previousPrice;
        this.inStock = inStock;
        this.imageUrl = imageUrl;
        this.route = route;
    }

    public Boolean isInStock()      { return inStock; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %.0f ₾%s | %s",
                storeName, productName, price,
                previousPrice != null ? " (was " + previousPrice.intValue() + " ₾)" : "",
                inStock ? "In Stock" : "Out of Stock");
    }
}