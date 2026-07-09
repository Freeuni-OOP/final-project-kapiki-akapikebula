package com.kapiki_akapikebula.app.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private CustomerDTO customer;
    private List<CartItemDTO> items;
    private BigDecimal totalAmount;

    @Getter
    @Setter
    public static class CustomerDTO {
        private String name;
        private String email;
        private String phone;
        private String address;
    }

    @Getter
    @Setter
    public static class CartItemDTO {
        private Long id;
        private int quantity;
        private BigDecimal minPrice;
    }
}
