package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.model.PriceAlert;
import com.kapiki_akapikebula.app.model.Product;
import com.kapiki_akapikebula.app.model.ShopProducts;
import com.kapiki_akapikebula.app.model.User;
import com.kapiki_akapikebula.app.repository.PriceAlertRepository;
import com.kapiki_akapikebula.app.repository.ShopProductsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PriceAlertNotificationServiceTest {
    @Mock
    private PriceAlertRepository priceAlertRepository;
    @Mock
    private ShopProductsRepository shopProductsRepository;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private PriceAlertNotificationService priceAlertNotificationService;

    private User user(String email) {
        User u = new User();
        u.setId(1L);
        u.setEmail(email);
        return u;
    }

    private Product product(long id, String name) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        return p;
    }

    private PriceAlert alert(User user, Product product, BigDecimal targetPrice,
                             boolean triggered, BigDecimal lastNotificationPrice) {
        PriceAlert alert = new PriceAlert();
        alert.setId(10L);
        alert.setUser(user);
        alert.setProduct(product);
        alert.setTargetPrice(targetPrice);
        alert.setTriggered(triggered);
        alert.setLastNotificationPrice(lastNotificationPrice);
        return alert;
    }

    private ShopProducts shopProductWithPrice(BigDecimal price) {
        ShopProducts sp = new ShopProducts();
        sp.setPrice(price);
        return sp;
    }

    @Test
    void checkAndNotify_ShouldSendEmail_WhenTargetReachedForFirstTime() {
        Product product = product(100L, "Samsung Galaxy S25");
        User user = user("test@user.com");
        PriceAlert alert = alert(user, product, BigDecimal.valueOf(500), false, null);

        when(priceAlertRepository.findAll()).thenReturn(List.of(alert));
        when(shopProductsRepository.findByProductId(100L))
                .thenReturn(List.of(shopProductWithPrice(BigDecimal.valueOf(450))));

        priceAlertNotificationService.checkAndNotify();

        verify(emailService, times(1)).sendSimpleMessage(eq("test@user.com"), anyString(), anyString());

        ArgumentCaptor<PriceAlert> captor = ArgumentCaptor.forClass(PriceAlert.class);
        verify(priceAlertRepository, times(1)).save(captor.capture());

        assertTrue(captor.getValue().isTriggered());
        assertEquals(BigDecimal.valueOf(450), captor.getValue().getLastNotificationPrice());
    }

    @Test
    void checkAndNotify_ShouldNotSendEmail_WhenTargetNotReached() {
        Product product = product(100L, "Samsung Galaxy S25");
        User user = user("test@user.com");
        PriceAlert alert = alert(user, product, BigDecimal.valueOf(400), false, null);

        when(priceAlertRepository.findAll()).thenReturn(List.of(alert));
        when(shopProductsRepository.findByProductId(100L))
                .thenReturn(List.of(shopProductWithPrice(BigDecimal.valueOf(450))));

        priceAlertNotificationService.checkAndNotify();

        verifyNoInteractions(emailService);
        verify(priceAlertRepository, never()).save(any());
    }

    @Test
    void checkAndNotify_ShouldNotSendEmail_WhenAlreadyNotifiedAtSamePrice() {
        Product product = product(100L, "Samsung Galaxy S25");
        User user = user("test@user.com");
        PriceAlert alert = alert(user, product, BigDecimal.valueOf(500), true, BigDecimal.valueOf(450));

        when(priceAlertRepository.findAll()).thenReturn(List.of(alert));
        when(shopProductsRepository.findByProductId(100L))
                .thenReturn(List.of(shopProductWithPrice(BigDecimal.valueOf(450))));

        priceAlertNotificationService.checkAndNotify();

        verifyNoInteractions(emailService);
        verify(priceAlertRepository, never()).save(any());
    }

    @Test
    void checkAndNotify_ShouldSendEmail_WhenPriceDroppedFurtherAfterAlreadyTriggered() {
        Product product = product(100L, "Samsung Galaxy S25");
        User user = user("test@user.com");
        PriceAlert alert = alert(user, product, BigDecimal.valueOf(500), true, BigDecimal.valueOf(450));

        when(priceAlertRepository.findAll()).thenReturn(List.of(alert));
        when(shopProductsRepository.findByProductId(100L))
                .thenReturn(List.of(shopProductWithPrice(BigDecimal.valueOf(400))));

        priceAlertNotificationService.checkAndNotify();

        verify(emailService, times(1)).sendSimpleMessage(eq("test@user.com"), anyString(), anyString());

        ArgumentCaptor<PriceAlert> captor = ArgumentCaptor.forClass(PriceAlert.class);
        verify(priceAlertRepository, times(1)).save(captor.capture());
        assertEquals(BigDecimal.valueOf(400), captor.getValue().getLastNotificationPrice());
    }

    @Test
    void checkAndNotify_ShouldNotSendEmail_WhenPriceRoseBackAboveLastNotification() {
        Product product = product(100L, "Samsung Galaxy S25");
        User user = user("test@user.com");
        PriceAlert alert = alert(user, product, BigDecimal.valueOf(500), true, BigDecimal.valueOf(400));

        when(priceAlertRepository.findAll()).thenReturn(List.of(alert));
        when(shopProductsRepository.findByProductId(100L))
                .thenReturn(List.of(shopProductWithPrice(BigDecimal.valueOf(450))));

        priceAlertNotificationService.checkAndNotify();

        verifyNoInteractions(emailService);
        verify(priceAlertRepository, never()).save(any());
    }

    @Test
    void checkAndNotify_ShouldSkipAlert_WhenNoShopListingsExist() {
        Product product = product(100L, "Samsung Galaxy S25");
        User user = user("test@user.com");
        PriceAlert alert = alert(user, product, BigDecimal.valueOf(500), false, null);

        when(priceAlertRepository.findAll()).thenReturn(List.of(alert));
        when(shopProductsRepository.findByProductId(100L)).thenReturn(List.of());

        priceAlertNotificationService.checkAndNotify();

        verifyNoInteractions(emailService);
        verify(priceAlertRepository, never()).save(any());
    }

    @Test
    void checkAndNotify_ShouldContinueProcessing_WhenOneAlertThrowsException() {
        Product badProduct = product(100L, "Bad Product");
        User user1 = user("bad@user.com");
        PriceAlert badAlert = alert(user1, badProduct, BigDecimal.valueOf(500), false, null);

        Product goodProduct = product(200L, "Good Product");
        User user2 = user("good@user.com");
        PriceAlert goodAlert = alert(user2, goodProduct, BigDecimal.valueOf(500), false, null);

        when(priceAlertRepository.findAll()).thenReturn(List.of(badAlert, goodAlert));

        // პირველი პროდუქტისთვის repository გამონაკლისს აგდებს
        when(shopProductsRepository.findByProductId(100L)).thenThrow(new RuntimeException("DB error"));
        when(shopProductsRepository.findByProductId(200L))
                .thenReturn(List.of(shopProductWithPrice(BigDecimal.valueOf(450))));

        priceAlertNotificationService.checkAndNotify();

        // მეორე, კარგი alert მაინც უნდა დამუშავდეს
        verify(emailService, times(1)).sendSimpleMessage(eq("good@user.com"), anyString(), anyString());
        verify(priceAlertRepository, times(1)).save(any());
    }
}