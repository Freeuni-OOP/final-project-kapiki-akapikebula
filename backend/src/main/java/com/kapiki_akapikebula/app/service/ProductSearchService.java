package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.dto.ProductListingResponse;
import com.kapiki_akapikebula.app.dto.ProductSearchResponse;
import com.kapiki_akapikebula.app.model.Product;
import com.kapiki_akapikebula.app.model.ShopProducts;
import com.kapiki_akapikebula.app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;

    // "price" now works because we handle it manually after fetching
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "brand", "price");

    public Page<ProductSearchResponse> search(
            String query,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sortBy,
            String sortDir,
            int page,
            int size
    ) {
        // Clean up inputs
        String keyword = (query == null || query.isBlank()) ? null : query.trim();
        boolean sortByPrice = "price".equalsIgnoreCase(sortBy);

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        // For price sorting, we sort in Java after fetching (explained below).
        // For name/brand, we let the DB sort correctly.
        String dbSortField = sortByPrice ? "name" : // placeholder — overridden in Java
                (ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "name");

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(size, 100),
                Sort.by(direction, dbSortField)
        );

        // --- Step 1: get a page of matching IDs from the DB ---
        // This is a simple query with no collection join, so SQL-level
        // pagination works correctly here
        Page<Long> idPage = productRepository.searchIds(keyword, minPrice, maxPrice, pageable);

        if (idPage.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // --- Step 2: fetch full product + shop data for those IDs ---
        // One query with IN clause — no N+1 problem
        List<Product> products = productRepository.findByIdsWithListings(idPage.getContent());

        // Restore the original ID order from Step 1, because the IN clause
        // doesn't guarantee order
        Map<Long, Product> productById = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<Product> orderedProducts = idPage.getContent().stream()
                .map(productById::get)
                .filter(Objects::nonNull)
                .toList();

        // --- Step 3: convert to DTOs ---
        List<ProductSearchResponse> results = orderedProducts.stream()
                .map(product -> {

                    // Sort listings cheapest first inside each product card
                    List<ProductListingResponse> listingDtos = product.getShopProducts().stream()
                            .filter(sp -> sp.getPrice() != null)
                            .sorted(Comparator.comparing(ShopProducts::getPrice))
                            .map(sp -> new ProductListingResponse(
                                    sp.getShop() != null ? sp.getShop().getName() : "Unknown",
                                    sp.getPrice(),
                                    sp.getStockStatus(),
                                    sp.getProductUrl()
                            ))
                            .toList();

                    // Cheapest listing is first since we sorted above
                    BigDecimal lowestPrice = listingDtos.isEmpty()
                            ? null
                            : listingDtos.get(0).getPrice();

                    return new ProductSearchResponse(
                            product.getId(),
                            product.getName(),
                            product.getBrand(),
                            product.getImageUrl(),
                            lowestPrice,
                            listingDtos
                    );
                })
                .toList();

        // --- Step 4: if sorting by price, re-sort the page results ---
        // We can't do this at SQL level (lowestPrice is computed here in Java),
        // so we sort the current page's results after building them.
        // Note: this sorts within the page, not globally — global price sorting
        // across all pages would require a DB-level subquery, which is a future optimization.
        if (sortByPrice) {
            Comparator<ProductSearchResponse> byPrice = Comparator.comparing(
                    r -> r.getLowestPrice() != null ? r.getLowestPrice() : BigDecimal.ZERO
            );
            results = results.stream()
                    .sorted(direction == Sort.Direction.DESC ? byPrice.reversed() : byPrice)
                    .toList();
        }

        // Wrap in a Page so the controller response includes totalPages, totalElements etc.
        return new PageImpl<>(results, pageable, idPage.getTotalElements());
    }
}