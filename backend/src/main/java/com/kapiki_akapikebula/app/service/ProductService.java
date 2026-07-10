package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.dto.ProductListingResponse;
import com.kapiki_akapikebula.app.dto.MatchedProductDTO; // 👈 შემოვიტანოთ DTO
import com.kapiki_akapikebula.app.model.Product;
import com.kapiki_akapikebula.app.model.ShopProducts;
import com.kapiki_akapikebula.app.repository.ProductRepository;
import com.kapiki_akapikebula.app.repository.ShopProductsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ShopProductsRepository shopProductsRep;
    private final ProductRepository productRepository;

    public ProductService(ShopProductsRepository shopProductsRep,
                          ProductRepository productRepository) {
        this.shopProductsRep = shopProductsRep;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<MatchedProductDTO> getMatchedProductsForHomePage() {
        return productRepository.findMatchedProductsForHomePage();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getProductById(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        Map<String, Object> productMap = new HashMap<>();
        productMap.put("id", product.getId());
        productMap.put("name", product.getName());
        productMap.put("description", product.getDescription());
        productMap.put("imageUrl", product.getImageUrl());

        Map<String, Object> categoryMap = new HashMap<>();
        if (product.getCategory() != null) {
            categoryMap.put("id", product.getCategory().getId());
            categoryMap.put("name", product.getCategory().getName());
        }
        productMap.put("category", categoryMap);

        return productMap;
    }

    public List<ProductListingResponse> getProductListings(long productId) {
        List<ShopProducts> listings = shopProductsRep.findByProductIdOrderByPriceAsc(productId);

        if (listings.isEmpty()) {
            return List.of();
        }

        return listings.stream()
                .map(listing -> new ProductListingResponse(
                        listing.getShop() != null ? listing.getShop().getName() : "Unknown Shop",
                        listing.getPrice(),
                        listing.getStockStatus(),
                        listing.getProductUrl()
                ))
                .toList();
    }
}