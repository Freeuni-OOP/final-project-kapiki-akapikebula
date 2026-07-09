package com.kapiki_akapikebula.app.controller;

import com.kapiki_akapikebula.app.dto.OrderRequest;
import com.kapiki_akapikebula.app.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody OrderRequest request) {
        orderService.createOrder(request);
        return ResponseEntity.ok(Map.of("message", "Order placed successfully"));
    }
}