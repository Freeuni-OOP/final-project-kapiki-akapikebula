package com.kapiki_akapikebula.app.repository;

import com.kapiki_akapikebula.app.model.Category;
import com.kapiki_akapikebula.app.model.Product;
import com.kapiki_akapikebula.app.model.Shop;
import com.kapiki_akapikebula.app.model.ShopProducts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        Category defaultCategory = new Category();
        defaultCategory.setName("Default");
        entityManager.persist(defaultCategory);

        testProduct = new Product();
        testProduct.setName("iPhone 15 Pro");
        testProduct.setBrand("Apple");
        testProduct.setCategory(defaultCategory);
        entityManager.persist(testProduct);

        Shop shop = new Shop();
        shop.setName("Test Shop");
        shop.setBaseUrl("http://test.com");
        entityManager.persist(shop);

        ShopProducts shopProduct = new ShopProducts();
        shopProduct.setProduct(testProduct);
        shopProduct.setShop(shop);
        shopProduct.setPrice(new BigDecimal("1200.00"));
        shopProduct.setStockStatus("IN_STOCK");
        shopProduct.setProductUrl("url");
        entityManager.persist(shopProduct);

        entityManager.flush();
    }

    @Test
    void search_ShouldReturnProduct_WhenNameMatchesAndPriceIsInRange() {
        Page<Product> result = productRepository.search(
                "iphone",
                new BigDecimal("1000.00"),
                new BigDecimal("1500.00"),
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("iPhone 15 Pro");
    }

    @Test
    void search_ShouldReturnEmpty_WhenPriceIsOutOfRange() {
        Page<Product> result = productRepository.search(
                "iphone",
                new BigDecimal("1500.00"),
                new BigDecimal("2000.00"),
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).isEmpty();
    }
}