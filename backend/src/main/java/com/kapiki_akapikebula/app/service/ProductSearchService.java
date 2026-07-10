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
        String keyword = (query == null || query.isBlank()) ? null : query.trim();
        boolean sortByPrice = "price".equalsIgnoreCase(sortBy);

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;


        String dbSortField = sortByPrice ? "name" :
                (ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "name");

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(size, 100),
                Sort.by(direction, dbSortField)
        );


        Page<Long> idPage;

        if (sortByPrice) {
            Pageable unsortedPageable = PageRequest.of(
                    Math.max(page, 0),
                    Math.min(size, 100)
            );
            if (direction == Sort.Direction.DESC) {
                idPage = productRepository.searchIdsSortByPriceDesc(keyword, minPrice, maxPrice, unsortedPageable);
            } else {
                idPage = productRepository.searchIdsSortByPriceAsc(keyword, minPrice, maxPrice, unsortedPageable);
            }
        } else {
            idPage = productRepository.searchIds(keyword, minPrice, maxPrice, pageable);
        }

        if (idPage.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }


        List<Product> products = productRepository.findByIdsWithListings(idPage.getContent());

        Map<Long, Product> productById = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<Product> orderedProducts = idPage.getContent().stream()
                .map(productById::get)
                .filter(Objects::nonNull)
                .toList();

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

        return new PageImpl<>(results, pageable, idPage.getTotalElements());
    }
}