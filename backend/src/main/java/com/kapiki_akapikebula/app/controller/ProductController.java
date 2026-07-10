package com.kapiki_akapikebula.app.controller;

import com.kapiki_akapikebula.app.dto.ProductListingResponse;
import com.kapiki_akapikebula.app.dto.ProductSearchResponse;
import com.kapiki_akapikebula.app.dto.MatchedProductDTO; // 👈 შემოვიტანოთ DTO
import com.kapiki_akapikebula.app.service.ProductSearchService;
import com.kapiki_akapikebula.app.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;
    private final ProductSearchService productSearchService;

    public ProductController(ProductService productService, ProductSearchService productSearchService) {
        this.productService = productService;
        this.productSearchService = productSearchService;
    }

    // 🟢 1. ახალი ენდფოინთი ჰოუმ ფეიჯისთვის (Spring პირდაპირ ამას დაამთხვევს და 400-ს აღარ ამოაგდებს)
    @GetMapping("/home-products")
    public ResponseEntity<List<MatchedProductDTO>> getHomeProducts() {
        return ResponseEntity.ok(productService.getMatchedProductsForHomePage());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred."));
        }
    }

    @GetMapping("/{id}/listings")
    public ResponseEntity<?> getProductListings(@PathVariable long id) {
        try {
            List<ProductListingResponse> listings = productService.getProductListings(id);
            return ResponseEntity.ok(listings);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred while fetching listings."));
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getProductHistory(@PathVariable long id) {
        try {
            return ResponseEntity.ok(productService.getProductHistory(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred."));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductSearchResponse>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<ProductSearchResponse> results = productSearchService.search(
                query, minPrice, maxPrice, sortBy, sortDir, page, size
        );
        return ResponseEntity.ok(results);
    }
}