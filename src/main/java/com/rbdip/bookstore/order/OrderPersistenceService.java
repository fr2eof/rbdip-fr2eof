package com.rbdip.bookstore.order;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderPersistenceService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderPersistenceService(
            CustomerRepository customerRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public Order save(CreateOrderRequest request, List<ResolvedOrderLine> lines) {
        Customer customer = customerRepository.save(Customer.fromFullName(
                request.customerFullName(), request.customerAddress(), request.customerPhone()));
        Order order = orderRepository.save(new Order(customer, "new"));

        for (ResolvedOrderLine line : lines) {
            orderItemRepository.save(new OrderItem(order.getId(), line.product(), line.quantity()));
        }

        return order;
    }
}
