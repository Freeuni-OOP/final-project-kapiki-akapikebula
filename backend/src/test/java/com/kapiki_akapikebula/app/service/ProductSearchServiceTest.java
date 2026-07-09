package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.dto.ProductSearchResponse;
import com.kapiki_akapikebula.app.model.Product;
import com.kapiki_akapikebula.app.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductSearchServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductSearchService productSearchService;

    @Test
    void search_ShouldReturnPageOfResults() {
        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("iPhone 15");
        mockProduct.setBrand("Apple");
        mockProduct.setImageUrl("http://image.url");
        mockProduct.setShopProducts(new ArrayList<>()); //for NPE

        Page<Product> mockedPage = new PageImpl<>(List.of(mockProduct));
        when(productRepository.search(any(), any(), any(), any(Pageable.class)))
                .thenReturn(mockedPage);
        Page<ProductSearchResponse> result = productSearchService.search(
                "iPhone", BigDecimal.ZERO, BigDecimal.valueOf(2000), "name", "asc", 0, 20
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("iPhone 15", result.getContent().get(0).getName());
        assertEquals("Apple", result.getContent().get(0).getBrand());
    }
}