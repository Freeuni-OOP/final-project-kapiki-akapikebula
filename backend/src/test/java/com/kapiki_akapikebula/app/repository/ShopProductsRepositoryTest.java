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
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShopProductsRepositoryTest {

    @Autowired
    private ShopProductsRepository shopProductsRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Product testProduct;
    private Shop shop1;
    private Shop shop2;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setName("Gaming");
        entityManager.persist(category);

        testProduct = new Product();
        testProduct.setName("Nintendo Switch");
        testProduct.setBrand("Nintendo");
        testProduct.setCategory(category);
        entityManager.persist(testProduct);

        shop1 = new Shop();
        shop1.setName("Zoommer");
        shop1.setBaseUrl("http://zoommer.ge");
        entityManager.persist(shop1);

        shop2 = new Shop();
        shop2.setName("Alta");
        shop2.setBaseUrl("http://alta.ge");
        entityManager.persist(shop2);

        ShopProducts sp1 = new ShopProducts();
        sp1.setProduct(testProduct);
        sp1.setShop(shop1);
        sp1.setPrice(new BigDecimal("1000.00"));
        sp1.setStockStatus("IN_STOCK");
        sp1.setProductUrl("url1");
        entityManager.persist(sp1);

        ShopProducts sp2 = new ShopProducts();
        sp2.setProduct(testProduct);
        sp2.setShop(shop2);
        sp2.setPrice(new BigDecimal("850.00"));
        sp2.setStockStatus("IN_STOCK");
        sp2.setProductUrl("url2");
        entityManager.persist(sp2);

        entityManager.flush();
    }

    @Test
    void findByProductIdOrderByPriceAsc_ShouldReturnProductsSortedByPrice() {
        List<ShopProducts> products = shopProductsRepository.findByProductIdOrderByPriceAsc(testProduct.getId());

        assertThat(products).hasSize(2);
        assertThat(products.get(0).getPrice()).isEqualByComparingTo("850.00");
        assertThat(products.get(0).getShop().getName()).isEqualTo("Alta");
        assertThat(products.get(1).getPrice()).isEqualByComparingTo("1000.00");
    }

    @Test
    void findByProductId_ShouldReturnAllShopProductsForGivenProduct() {
        List<ShopProducts> products = shopProductsRepository.findByProductId(testProduct.getId());

        assertThat(products).hasSize(2);
    }

    @Test
    void findByProductIdAndShopId_ShouldReturnSpecificShopProduct() {
        Optional<ShopProducts> result = shopProductsRepository.findByProductIdAndShopId(testProduct.getId(), shop1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getPrice()).isEqualByComparingTo("1000.00");
        assertThat(result.get().getShop().getName()).isEqualTo("Zoommer");
    }

    @Test
    void findByProductIdAndShopId_ShouldReturnEmpty_WhenNotInShop() {
        Optional<ShopProducts> result = shopProductsRepository.findByProductIdAndShopId(testProduct.getId(), 999L);

        assertThat(result).isEmpty();
    }
}