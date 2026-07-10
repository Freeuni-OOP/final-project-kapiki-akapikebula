package com.kapiki_akapikebula.app.repository;

import com.kapiki_akapikebula.app.model.Category;
import com.kapiki_akapikebula.app.model.PriceAlert;
import com.kapiki_akapikebula.app.model.Product;
import com.kapiki_akapikebula.app.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PriceAlertRepositoryTest {

    @Autowired
    private PriceAlertRepository priceAlertRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Product testProduct;
    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setUsername("testuser_123");
        testUser.setEmail("testuser@example.com");
        testUser.setPasswordHash("hashed_password_123");
        entityManager.persist(testUser);

        Category category = new Category();
        category.setName("Smartphones");
        entityManager.persist(category);

        testProduct = new Product();
        testProduct.setName("Samsung Galaxy S24");
        testProduct.setBrand("Samsung");
        testProduct.setCategory(category);
        entityManager.persist(testProduct);

        PriceAlert alert = new PriceAlert();
        alert.setUser(testUser);
        alert.setProduct(testProduct);
        alert.setTargetPrice(new BigDecimal("900.00"));
        alert.setTriggered(false);
        entityManager.persist(alert);

        entityManager.flush();
    }

    @Test
    void save_ShouldPersistPriceAlert() {
        PriceAlert newAlert = new PriceAlert();
        newAlert.setUser(testUser);
        newAlert.setProduct(testProduct);
        newAlert.setTargetPrice(new BigDecimal("850.00"));
        newAlert.setTriggered(false);

        PriceAlert savedAlert = priceAlertRepository.save(newAlert);

        assertThat(savedAlert.getId()).isNotNull();
        assertThat(savedAlert.getTargetPrice()).isEqualByComparingTo("850.00");
        assertThat(savedAlert.isTriggered()).isFalse();
    }

    @Test
    void findAll_ShouldReturnSavedPriceAlerts() {
        List<PriceAlert> alerts = priceAlertRepository.findAll();

        assertThat(alerts).isNotEmpty();
        assertThat(alerts).hasSize(1);
        assertThat(alerts.get(0).getTargetPrice()).isEqualByComparingTo("900.00");
    }
}