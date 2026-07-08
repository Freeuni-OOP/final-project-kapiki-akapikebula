package com.kapiki_akapikebula.app.scraper;

import java.util.Map;

public class StoreListing {
    private String id;
    private String storeName;
    private String productName;
    private double price;
    private Double previousPrice;
    private Boolean inStock;
    private String imageUrl;
    private String route;
    private String matchKey;
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

    // Getters
    public String getId()           { return id; }
    public String getStoreName()    { return storeName; }
    public String getProductName()  { return productName; }
    public double getPrice()        { return price; }
    public Double getPreviousPrice(){ return previousPrice; }
    public Boolean isInStock()      { return inStock; }
    public String getImageUrl()     { return imageUrl; }
    public String getRoute()        { return route; }
    public String getMatchKey()     { return matchKey; }
    public Map<String, String> getAttributes() { return attributes; }

    // Setters for fields populated after construction
    public void setMatchKey(String matchKey)             { this.matchKey = matchKey; }
    public void setAttributes(Map<String, String> attrs) { this.attributes = attrs; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %.0f ₾%s | %s",
                storeName, productName, price,
                previousPrice != null ? " (was " + previousPrice.intValue() + " ₾)" : "",
                inStock ? "In Stock" : "Out of Stock");
    }
}