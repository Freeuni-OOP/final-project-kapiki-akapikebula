package com.kapiki_akapikebula.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kapiki_akapikebula.app.dto.WatchlistRequest;
import com.kapiki_akapikebula.app.dto.WatchlistResponse;
import com.kapiki_akapikebula.app.service.JwtUtil;
import com.kapiki_akapikebula.app.service.WatchlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WatchlistController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WatchlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WatchlistService watchlistService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addToWatchlist_ShouldReturn200_WhenSuccessful() throws Exception {
        String mockToken = "Bearer fake-jwt-token-strings";
        String mockEmail = "test@user.com";
        WatchlistRequest request = new WatchlistRequest();

        when(jwtUtil.getEmailFromToken("fake-jwt-token-strings")).thenReturn(mockEmail);
        when(watchlistService.addToWatchlist(eq(mockEmail), any(WatchlistRequest.class)))
                .thenReturn(new WatchlistResponse(1L, 100L, "iPhone", "url", BigDecimal.TEN, BigDecimal.ONE, false));

        mockMvc.perform(post("/api/watchlist")
                        .header("Authorization", mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getWatchlist_ShouldReturn200_WhenSuccessful() throws Exception {
        String mockToken = "Bearer fake-jwt-token-strings";
        String mockEmail = "test@user.com";

        when(jwtUtil.getEmailFromToken("fake-jwt-token-strings")).thenReturn(mockEmail);
        when(watchlistService.getWatchlist(mockEmail)).thenReturn(List.of());

        mockMvc.perform(get("/api/watchlist")
                        .header("Authorization", mockToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}