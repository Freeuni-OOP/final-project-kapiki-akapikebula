package com.kapiki_akapikebula.app.repository;

import com.kapiki_akapikebula.app.model.User;
import com.kapiki_akapikebula.app.model.VerificationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VerificationTokenRepositoryTest {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    private VerificationToken testToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        // 1. ვქმნით იუზერს ყველა სავალდებულო ველით
        testUser = new User();
        testUser.setUsername("token_user");
        testUser.setEmail("tokenuser@example.com");
        testUser.setPasswordHash("hash_123");
        entityManager.persist(testUser);

        // 2. ვქმნით ტოკენს და ვუმატებთ სავალდებულო expiryDate-ს
        testToken = new VerificationToken();
        testToken.setToken("random-uuid-token-12345");
        testToken.setUser(testUser);

        // ვუთითებთ ვადის გასვლის დროს (მაგალითად: ხვალინდელი დრო)
        testToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        entityManager.persist(testToken);
        entityManager.flush();
    }

    @Test
    void findByToken_ShouldReturnVerificationToken_WhenTokenExists() {
        Optional<VerificationToken> foundToken = verificationTokenRepository.findByToken("random-uuid-token-12345");

        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getToken()).isEqualTo("random-uuid-token-12345");
        assertThat(foundToken.get().getUser().getUsername()).isEqualTo("token_user");
    }

    @Test
    void findByToken_ShouldReturnEmpty_WhenTokenDoesNotExist() {
        Optional<VerificationToken> foundToken = verificationTokenRepository.findByToken("invalid-token");

        assertThat(foundToken).isEmpty();
    }
}