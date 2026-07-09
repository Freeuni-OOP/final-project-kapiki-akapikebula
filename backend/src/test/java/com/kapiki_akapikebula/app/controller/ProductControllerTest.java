package com.kapiki_akapikebula.app.controller;

import com.kapiki_akapikebula.app.service.JwtUtil;
import com.kapiki_akapikebula.app.service.ProductSearchService;
import com.kapiki_akapikebula.app.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private ProductSearchService productSearchService;
    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void getProductListings_ShouldReturn200_WhenSuccessful() throws Exception {
        long productId = 1L;
        when(productService.getProductListings(productId)).thenReturn(List.of());

        mockMvc.perform(get("/api/products/{id}/listings", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void search_ShouldReturn200_WhenSuccessful() throws Exception {
        when(productSearchService.search(any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/products/search")
                        .param("query", "iPhone")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}