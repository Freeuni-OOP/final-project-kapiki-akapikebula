package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.dto.PriceHistoryResponse;
import com.kapiki_akapikebula.app.model.PriceHistory;
import com.kapiki_akapikebula.app.model.Product;
import com.kapiki_akapikebula.app.repository.PriceHistoryRepository;
import com.kapiki_akapikebula.app.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PriceHistoryServiceTest {
    @Mock
    private PriceHistoryRepository priceHistoryRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private PriceHistoryService priceHistoryService;

    @Test
    void getPriceHistoryById_ShouldReturnList_WhenStartDateIsNull() {
        Long productId = 1L;
        PriceHistory history = new PriceHistory();
        history.setPrice(BigDecimal.TEN);
        history.setRecordedAt(LocalDateTime.now());

        when(priceHistoryRepository.findByProductIdOrderByRecordedAtAsc(productId)).thenReturn(List.of(history));

        List<PriceHistoryResponse> result = priceHistoryService.getPriceHistoryById(productId, null);

        assertFalse(result.isEmpty());
        assertEquals(BigDecimal.TEN, result.get(0).getPrice());
        verify(priceHistoryRepository, times(1)).findByProductIdOrderByRecordedAtAsc(productId);
    }
    @Test
    void savePriceHistory_ShouldSave_WhenProductExists() {
        Long productId = 1L;
        BigDecimal newPrice = BigDecimal.valueOf(100);
        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        priceHistoryService.savePriceHistory(productId, newPrice);

        verify(priceHistoryRepository, times(1)).save(any(PriceHistory.class));
    }
}