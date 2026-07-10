package com.kapiki_akapikebula.app.dto;

import java.math.BigDecimal;

public interface MatchedProductDTO {
    Long getId();
    String getName();
    String getImageUrl();
    BigDecimal getMinPrice();
    BigDecimal getMaxPrice();
}
