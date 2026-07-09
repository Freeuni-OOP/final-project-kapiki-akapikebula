package com.kapiki_akapikebula.app.controller;

import com.kapiki_akapikebula.app.dto.OrderRequest;
import com.kapiki_akapikebula.app.model.Order;
import com.kapiki_akapikebula.app.model.OrderItem;
import com.kapiki_akapikebula.app.model.Product;
import com.kapiki_akapikebula.app.repository.OrderRepository;
import com.kapiki_akapikebula.app.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderController(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomer().getName());
        order.setCustomerEmail(request.getCustomer().getEmail());
        order.setCustomerPhone(request.getCustomer().getPhone());
        order.setShippingAddress(request.getCustomer().getAddress());
        order.setTotalAmount(request.getTotalAmount());

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderRequest.CartItemDTO itemDTO : request.getItems()) {
            Product product = productRepository.findById(itemDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDTO.getId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(itemDTO.getMinPrice());
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        orderRepository.save(order);

        return ResponseEntity.ok().body("{\"message\": \"Order placed successfully\"}");
    }
}