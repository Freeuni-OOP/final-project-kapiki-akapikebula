package com.kapiki_akapikebula.app.controller;

import com.kapiki_akapikebula.app.dto.PriceHistoryResponse;
import com.kapiki_akapikebula.app.service.JwtUtil;
import com.kapiki_akapikebula.app.service.PriceHistoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PriceHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PriceHistoryService priceHistoryService;
    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void getProductPriceHistory_ShouldReturn200_WhenSuccessful() throws Exception {
        Long productId = 1L;
        PriceHistoryResponse mockResponse = Mockito.mock(PriceHistoryResponse.class);
        List<PriceHistoryResponse> mockList = List.of(mockResponse);

        when(priceHistoryService.getPriceHistoryById(eq(productId), any()))
                .thenReturn(mockList);

        mockMvc.perform(get("/api/prices/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getProductPriceHistory_ShouldReturn400_WhenExceptionThrown() throws Exception {
        Long productId = 2L;
        when(priceHistoryService.getPriceHistoryById(eq(productId), any()))
                .thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(get("/api/prices/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}