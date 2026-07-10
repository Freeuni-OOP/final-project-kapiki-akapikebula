package com.kapiki_akapikebula.app.repository;

import com.kapiki_akapikebula.app.model.Shop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShopRepositoryTest {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Shop testShop;

    @BeforeEach
    void setUp() {
        testShop = new Shop();
        testShop.setName("Zoommer");
        testShop.setBaseUrl("http://zoommer.ge");

        entityManager.persist(testShop);
        entityManager.flush();
    }

    @Test
    void findByName_ShouldReturnShop_WhenNameExists() {
        Optional<Shop> foundShop = shopRepository.findByName("Zoommer");

        assertThat(foundShop).isPresent();
        assertThat(foundShop.get().getName()).isEqualTo("Zoommer");
        assertThat(foundShop.get().getBaseUrl()).isEqualTo("http://zoommer.ge");
    }

    @Test
    void findByName_ShouldReturnEmpty_WhenNameDoesNotExist() {
        Optional<Shop> foundShop = shopRepository.findByName("Alta");

        assertThat(foundShop).isEmpty();
    }
}