package com.kapiki_akapikebula.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductListingResponse {
    public String shopName;
    public BigDecimal price;
    public String stockStatus;
    public String productUrl;
}