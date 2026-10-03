package com.rbdip.bookstore.order;

import com.rbdip.bookstore.review.PurchaseLookup;
import org.springframework.stereotype.Component;

@Component
public class OrderPurchaseLookup implements PurchaseLookup {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderPurchaseLookup(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public boolean hasAnyOrdersAndItems() {
        return orderRepository.count() > 0 && orderItemRepository.count() > 0;
    }
}
