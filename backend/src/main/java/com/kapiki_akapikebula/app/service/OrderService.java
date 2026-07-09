package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.dto.OrderRequest;
import com.kapiki_akapikebula.app.model.Order;
import com.kapiki_akapikebula.app.model.OrderItem;
import com.kapiki_akapikebula.app.model.Product;
import com.kapiki_akapikebula.app.repository.OrderRepository;
import com.kapiki_akapikebula.app.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void createOrder(OrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomer().getName());
        order.setCustomerEmail(request.getCustomer().getEmail());
        order.setCustomerPhone(request.getCustomer().getPhone());
        order.setShippingAddress(request.getCustomer().getAddress());
        order.setTotalAmount(request.getTotalAmount());

        List<Long> productIds = request.getItems().stream()
                .map(OrderRequest.CartItemDTO::getId)
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(productIds);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderRequest.CartItemDTO itemDTO : request.getItems()) {
            Product product = productMap.get(itemDTO.getId());
            if (product == null) {
                throw new RuntimeException("Product not found with id: " + itemDTO.getId());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(itemDTO.getMinPrice());
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        orderRepository.save(order);
    }
}
