package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.dto.WatchlistRequest;
import com.kapiki_akapikebula.app.dto.WatchlistResponse;
import com.kapiki_akapikebula.app.model.PriceAlert;
import com.kapiki_akapikebula.app.model.Product;
import com.kapiki_akapikebula.app.model.User;
import com.kapiki_akapikebula.app.repository.PriceAlertRepository;
import com.kapiki_akapikebula.app.repository.ProductRepository;
import com.kapiki_akapikebula.app.repository.ShopProductsRepository;
import com.kapiki_akapikebula.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WatchlistServiceTest {

    @Mock
    private PriceAlertRepository priceAlertRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ShopProductsRepository shopProductsRepository;

    @InjectMocks
    private WatchlistService watchlistService;

    @Test
    void addToWatchlist_ShouldSaveAlert_WhenSuccessful() {
        String email = "test@user.com";
        WatchlistRequest request = new WatchlistRequest();
        request.setProductID(100L);
        request.setTargetPrice(BigDecimal.valueOf(500));

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(100L);
        product.setName("Test Product");

        PriceAlert savedAlert = new PriceAlert();
        savedAlert.setId(10L);
        savedAlert.setTargetPrice(request.getTargetPrice());
        savedAlert.setProduct(product);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(priceAlertRepository.findByUserIdAndProductId(1L, 100L)).thenReturn(Optional.empty());
        when(shopProductsRepository.findByProductId(100L)).thenReturn(List.of());
        when(priceAlertRepository.save(any(PriceAlert.class))).thenReturn(savedAlert);

        WatchlistResponse response = watchlistService.addToWatchlist(email, request);

        assertNotNull(response);
        assertEquals(10L, response.getAlertId());
        assertEquals(100L, response.getProductId());
        verify(priceAlertRepository, times(1)).save(any(PriceAlert.class));
    }

    @Test
    void addToWatchlist_ShouldThrowException_WhenAlreadyInWatchlist() {
        String email = "test@user.com";
        WatchlistRequest request = new WatchlistRequest();
        request.setProductID(100L);

        User user = new User();
        user.setId(1L);
        Product product = new Product();
        product.setId(100L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(priceAlertRepository.findByUserIdAndProductId(1L, 100L)).thenReturn(Optional.of(new PriceAlert()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> watchlistService.addToWatchlist(email, request));
        assertEquals("This product is already in your watchlist.", exception.getMessage());
    }
}