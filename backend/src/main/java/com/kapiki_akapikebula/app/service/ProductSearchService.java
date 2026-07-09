package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.dto.ProductListingResponse;
import com.kapiki_akapikebula.app.dto.ProductSearchResponse;
import com.kapiki_akapikebula.app.model.Product;
import com.kapiki_akapikebula.app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "brand");

    public Page<ProductSearchResponse> search(
            String query,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sortBy,
            String sortDir,
            int page,
            int size
    ) {
        String keyword = (query == null || query.isBlank()) ? null : query.trim();
        String sortField = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "name";

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(size, 100),
                Sort.by(direction, sortField)
        );
        Page<Product> matchingProducts = productRepository.search(
                keyword, minPrice, maxPrice, pageable
        );

        List<ProductSearchResponse> results = matchingProducts.getContent().stream()
                .map(product -> {

                    List<ProductListingResponse> listingDtos = product.getShopProducts().stream()
                            .filter(sp -> sp.getPrice() != null)
                            .sorted(Comparator.comparing(sp -> sp.getPrice()))
                            .map(sp -> new ProductListingResponse(
                                    sp.getShop() != null ? sp.getShop().getName() : "Unknown Shop",
                                    sp.getPrice(),
                                    sp.getStockStatus(),
                                    sp.getProductUrl()
                            ))
                            .toList();

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

        return new PageImpl<>(results, pageable, matchingProducts.getTotalElements());
    }
}