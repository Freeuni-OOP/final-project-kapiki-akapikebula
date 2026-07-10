package com.kapiki_akapikebula.app.service;
import com.kapiki_akapikebula.app.model.PriceAlert;
import com.kapiki_akapikebula.app.model.ShopProducts;
import com.kapiki_akapikebula.app.repository.PriceAlertRepository;
import com.kapiki_akapikebula.app.repository.ShopProductsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceAlertNotificationService {

    private final PriceAlertRepository priceAlertRepository;
    private final ShopProductsRepository shopProductsRepository;
    private final EmailService emailService;


    public void checkAndNotify() {
        List<PriceAlert> alerts = priceAlertRepository.findAll();

        int notified = 0;
        int skipped = 0;

        for (PriceAlert alert : alerts) {
            try {
                if (processAlert(alert)) {
                    notified++;
                } else {
                    skipped++;
                }
            } catch (Exception e) {
                log.error("Failed to process price alert id={}: {}", alert.getId(), e.getMessage());
                skipped++;
            }
        }

        log.info("Price alert check complete — notified: {}, skipped: {}", notified, skipped);
    }

    private boolean processAlert(PriceAlert alert) {
        BigDecimal currentPrice = getCurrentLowestPrice(alert.getProduct().getId());

        if (currentPrice == null) {
            return false;
        }

        boolean targetReached = currentPrice.compareTo(alert.getTargetPrice()) <= 0;
        if (!targetReached) {
            return false;
        }

        boolean firstNotification = !alert.isTriggered() || alert.getLastNotificationPrice() == null;
        boolean droppedFurther = alert.getLastNotificationPrice() != null
                && currentPrice.compareTo(alert.getLastNotificationPrice()) < 0;

        if (!firstNotification && !droppedFurther) {
            return false;
        }

        sendNotification(alert, currentPrice);

        alert.setLastNotificationPrice(currentPrice);
        alert.setTriggered(true);
        priceAlertRepository.save(alert);

        return true;
    }

    private BigDecimal getCurrentLowestPrice(long productId) {
        List<ShopProducts> shopPrices = shopProductsRepository.findByProductId(productId);

        return shopPrices.stream()
                .map(ShopProducts::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(null);
    }

    private void sendNotification(PriceAlert alert, BigDecimal currentPrice) {
        String email = alert.getUser().getEmail();
        String productName = alert.getProduct().getName();

        String subject = "Price Change: " + productName;
        String text = String.format(
                "The price of the product \"%s\" is now %s (your desired price is %s).",
                productName, currentPrice, alert.getTargetPrice()
        );

        emailService.sendSimpleMessage(email, subject, text);
    }
}