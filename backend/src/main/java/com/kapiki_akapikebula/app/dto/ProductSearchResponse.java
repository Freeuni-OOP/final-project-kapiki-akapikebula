package com.kapiki_akapikebula.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductSearchResponse {
    public Long productId;
    public String name;
    public String brand;
    public String imageUrl;
    public BigDecimal lowestPrice;
    public List<ProductListingResponse> listings;
}