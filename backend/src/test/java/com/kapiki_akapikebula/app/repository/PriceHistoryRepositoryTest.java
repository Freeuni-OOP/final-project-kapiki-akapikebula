package com.kapiki_akapikebula.app.repository;

import com.kapiki_akapikebula.app.model.Category;
import com.kapiki_akapikebula.app.model.PriceHistory;
import com.kapiki_akapikebula.app.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PriceHistoryRepositoryTest {

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Product testProduct;

    @BeforeEach
    void setUp() throws InterruptedException {
        Category category = new Category();
        category.setName("Electronics");
        entityManager.persist(category);

        testProduct = new Product();
        testProduct.setName("PlayStation 5");
        testProduct.setBrand("Sony");
        testProduct.setCategory(category);
        entityManager.persist(testProduct);

        PriceHistory history1 = new PriceHistory();
        history1.setProduct(testProduct);
        history1.setPrice(new BigDecimal("1500.00"));
        entityManager.persist(history1);
        entityManager.flush();

        Thread.sleep(10);

        // 4. ვინახავთ მეორე, განახლებულ ფასს
        PriceHistory history2 = new PriceHistory();
        history2.setProduct(testProduct);
        history2.setPrice(new BigDecimal("1450.00"));
        entityManager.persist(history2);

        entityManager.flush();
    }

    @Test
    void findByProductIdOrderByRecordedAtAsc_ShouldReturnSortedHistories() {
        List<PriceHistory> histories = priceHistoryRepository.findByProductIdOrderByRecordedAtAsc(testProduct.getId());

        assertThat(histories).hasSize(2);
        assertThat(histories.get(0).getProduct().getId()).isEqualTo(testProduct.getId());
        assertThat(histories.get(0).getPrice()).isEqualByComparingTo("1500.00");
        assertThat(histories.get(1).getPrice()).isEqualByComparingTo("1450.00");
    }

    @Test
    void getPriceHistory_ShouldReturnRecords_WhenStartDateIsBeforeCreation() {
        LocalDateTime pastDate = LocalDateTime.now().minusMinutes(5);

        List<PriceHistory> histories = priceHistoryRepository.getPriceHistory(testProduct.getId(), pastDate);

        assertThat(histories).hasSize(2);
    }

    @Test
    void getPriceHistory_ShouldReturnEmpty_WhenStartDateIsAfterCreation() {
        LocalDateTime futureDate = LocalDateTime.now().plusMinutes(5);

        List<PriceHistory> histories = priceHistoryRepository.getPriceHistory(testProduct.getId(), futureDate);

        assertThat(histories).isEmpty();
    }
}