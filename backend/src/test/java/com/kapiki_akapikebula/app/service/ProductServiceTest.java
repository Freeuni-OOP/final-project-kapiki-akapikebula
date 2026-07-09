package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.dto.ProductListingResponse;
import com.kapiki_akapikebula.app.model.Shop;
import com.kapiki_akapikebula.app.model.ShopProducts;
import com.kapiki_akapikebula.app.repository.ShopProductsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ShopProductsRepository shopProductsRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getProductListings_ShouldReturnMappedList_WhenListingsExist() {
        long productId = 100L;

        Shop shop = new Shop();
        shop.setName("Alta.ge");

        ShopProducts listing1 = new ShopProducts();
        listing1.setShop(shop);
        listing1.setPrice(BigDecimal.valueOf(999.99));
        listing1.setStockStatus("IN_STOCK");
        listing1.setProductUrl("https://alta.ge/product/100");

        when(shopProductsRepository.findByProductIdOrderByPriceAsc(productId))
                .thenReturn(List.of(listing1));

        List<ProductListingResponse> result = productService.getProductListings(productId);

        assertEquals(1, result.size());
        assertEquals("Alta.ge", result.get(0).getShopName());
        assertEquals(BigDecimal.valueOf(999.99), result.get(0).getPrice());
        assertEquals("IN_STOCK", result.get(0).getStockStatus());
        assertEquals("https://alta.ge/product/100", result.get(0).getProductUrl());
    }

    @Test
    void getProductListings_ShouldUseUnknownShop_WhenShopIsNull() {
        long productId = 100L;

        ShopProducts listing = new ShopProducts();
        listing.setShop(null);
        listing.setPrice(BigDecimal.valueOf(50));
        listing.setStockStatus("OUT_OF_STOCK");
        listing.setProductUrl("https://example.com/p");

        when(shopProductsRepository.findByProductIdOrderByPriceAsc(productId))
                .thenReturn(List.of(listing));

        List<ProductListingResponse> result = productService.getProductListings(productId);

        assertEquals(1, result.size());
        assertEquals("Unknown Shop", result.get(0).getShopName());
    }

    @Test
    void getProductListings_ShouldThrowException_WhenNoListingsFound() {
        long productId = 999L;

        when(shopProductsRepository.findByProductIdOrderByPriceAsc(productId))
                .thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.getProductListings(productId));

        assertEquals("Product listings not found for ID: " + productId, exception.getMessage());
        verify(shopProductsRepository, times(1)).findByProductIdOrderByPriceAsc(productId);
    }

    @Test
    void getProductListings_ShouldPreserveOrder_WhenMultipleListingsExist() {
        long productId = 100L;

        Shop shopA = new Shop();
        shopA.setName("Shop A");
        ShopProducts cheap = new ShopProducts();
        cheap.setShop(shopA);
        cheap.setPrice(BigDecimal.valueOf(100));
        cheap.setStockStatus("IN_STOCK");
        cheap.setProductUrl("url-a");

        Shop shopB = new Shop();
        shopB.setName("Shop B");
        ShopProducts expensive = new ShopProducts();
        expensive.setShop(shopB);
        expensive.setPrice(BigDecimal.valueOf(200));
        expensive.setStockStatus("IN_STOCK");
        expensive.setProductUrl("url-b");

        when(shopProductsRepository.findByProductIdOrderByPriceAsc(productId))
                .thenReturn(List.of(cheap, expensive));

        List<ProductListingResponse> result = productService.getProductListings(productId);

        assertEquals(2, result.size());
        assertEquals("Shop A", result.get(0).getShopName());
        assertEquals("Shop B", result.get(1).getShopName());
    }
}