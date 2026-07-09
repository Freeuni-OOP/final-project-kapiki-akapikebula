package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.model.Category;
import com.kapiki_akapikebula.app.model.Product;
import com.kapiki_akapikebula.app.model.Shop;
import com.kapiki_akapikebula.app.model.ShopProducts;
import com.kapiki_akapikebula.app.repository.CategoryRepository;
import com.kapiki_akapikebula.app.repository.ProductRepository;
import com.kapiki_akapikebula.app.repository.ShopProductsRepository;
import com.kapiki_akapikebula.app.repository.ShopRepository;
import com.kapiki_akapikebula.app.scraper.StoreListing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
public class ScraperIngestionServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ShopProductsRepository shopProductsRepository;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ScraperIngestionService scraperIngestionService;

    private StoreListing mockListing(String name, double price, boolean inStock, String route, String imageUrl) {
        StoreListing listing = mock(StoreListing.class);
        lenient().when(listing.getProductName()).thenReturn(name);
        lenient().when(listing.getPrice()).thenReturn(price);
        lenient().when(listing.isInStock()).thenReturn(inStock);
        lenient().when(listing.getRoute()).thenReturn(route);
        lenient().when(listing.getImageUrl()).thenReturn(imageUrl);
        return listing;
    }

    @Test
    void ingest_ShouldThrowException_WhenShopNotFound() {
        when(shopRepository.findByName("UnknownShop")).thenReturn(Optional.empty());

        StoreListing listing = mockListing("Samsung Galaxy S25", 999.99, true, "/p/1", "img.jpg");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> scraperIngestionService.ingest(List.of(listing), "UnknownShop"));

        assertTrue(exception.getMessage().contains("Shop not found"));
        verifyNoInteractions(categoryRepository, productRepository, shopProductsRepository);
    }

    @Test
    void ingest_ShouldThrowException_WhenDefaultCategoryNotFound() {
        Shop shop = new Shop();
        shop.setId(1L);

        when(shopRepository.findByName("Alta.ge")).thenReturn(Optional.of(shop));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        StoreListing listing = mockListing("Samsung Galaxy S25", 999.99, true, "/p/1", "img.jpg");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> scraperIngestionService.ingest(List.of(listing), "Alta.ge"));

        assertTrue(exception.getMessage().contains("Default category not found"));
        verifyNoInteractions(productRepository, shopProductsRepository);
    }

    @Test
    void ingest_ShouldCreateNewProductAndShopListing_WhenNotExists() {
        Shop shop = new Shop();
        shop.setId(1L);

        Category category = new Category();
        category.setId(1L);

        Product savedProduct = new Product();
        savedProduct.setId(50L);

        when(shopRepository.findByName("Alta.ge")).thenReturn(Optional.of(shop));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findByMatchKey("samsung galaxy s25")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(shopProductsRepository.findByProductIdAndShopId(50L, 1L)).thenReturn(Optional.empty());
        when(shopProductsRepository.save(any(ShopProducts.class))).thenAnswer(inv -> inv.getArgument(0));

        StoreListing listing = mockListing("Samsung Galaxy S25", 999.99, true, "/p/1", "img.jpg");

        scraperIngestionService.ingest(List.of(listing), "Alta.ge");

        verify(productRepository, times(1)).save(any(Product.class));
        verify(shopProductsRepository, times(1)).save(any(ShopProducts.class));
    }

    @Test
    void ingest_ShouldReuseExistingProductAndUpdateListing_WhenAlreadyExists() {
        Shop shop = new Shop();
        shop.setId(1L);

        Category category = new Category();
        category.setId(1L);

        Product existingProduct = new Product();
        existingProduct.setId(50L);

        ShopProducts existingShopProduct = new ShopProducts();
        existingShopProduct.setProduct(existingProduct);
        existingShopProduct.setShop(shop);

        when(shopRepository.findByName("Alta.ge")).thenReturn(Optional.of(shop));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findByMatchKey("samsung galaxy s25")).thenReturn(Optional.of(existingProduct));
        when(shopProductsRepository.findByProductIdAndShopId(50L, 1L)).thenReturn(Optional.of(existingShopProduct));
        when(shopProductsRepository.save(any(ShopProducts.class))).thenAnswer(inv -> inv.getArgument(0));

        StoreListing listing = mockListing("Samsung Galaxy S25", 1099.50, false, "/p/1-new", "img.jpg");

        scraperIngestionService.ingest(List.of(listing), "Alta.ge");

        verify(productRepository, never()).save(any(Product.class));

        ArgumentCaptor<ShopProducts> captor = ArgumentCaptor.forClass(ShopProducts.class);
        verify(shopProductsRepository, times(1)).save(captor.capture());

        ShopProducts saved = captor.getValue();
        assertEquals(BigDecimal.valueOf(1099.50), saved.getPrice());
        assertEquals("OUT_OF_STOCK", saved.getStockStatus());
        assertEquals("/p/1-new", saved.getProductUrl());
        assertNotNull(saved.getLastUpdated());
    }

    @Test
    void ingest_ShouldSkipBadListing_AndContinueWithNextListing() {
        Shop shop = new Shop();
        shop.setId(1L);

        Category category = new Category();
        category.setId(1L);

        when(shopRepository.findByName("Alta.ge")).thenReturn(Optional.of(shop));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        StoreListing badListing = mock(StoreListing.class);
        when(badListing.getProductName()).thenReturn(null);

        Product goodProduct = new Product();
        goodProduct.setId(60L);

        StoreListing goodListing = mockListing("iPhone 17", 1999.00, true, "/p/2", "img2.jpg");

        when(productRepository.findByMatchKey("iphone 17")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(goodProduct);
        when(shopProductsRepository.findByProductIdAndShopId(60L, 1L)).thenReturn(Optional.empty());
        when(shopProductsRepository.save(any(ShopProducts.class))).thenAnswer(inv -> inv.getArgument(0));

        scraperIngestionService.ingest(List.of(badListing, goodListing), "Alta.ge");
        verify(productRepository, times(1)).save(any(Product.class));
        verify(shopProductsRepository, times(1)).save(any(ShopProducts.class));
    }
}