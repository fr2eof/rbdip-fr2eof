package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderValidator orderValidator;
    private final OrderLineItemResolver orderLineItemResolver;
    private final PricingCalculator pricingCalculator;
    private final OrderPersistenceService orderPersistenceService;
    private final OrderConfirmationNotifier orderConfirmationNotifier;

    public OrderService(
            OrderValidator orderValidator,
            OrderLineItemResolver orderLineItemResolver,
            PricingCalculator pricingCalculator,
            OrderPersistenceService orderPersistenceService,
            OrderConfirmationNotifier orderConfirmationNotifier) {
        this.orderValidator = orderValidator;
        this.orderLineItemResolver = orderLineItemResolver;
        this.pricingCalculator = pricingCalculator;
        this.orderPersistenceService = orderPersistenceService;
        this.orderConfirmationNotifier = orderConfirmationNotifier;
    }

    public Order createOrder(CreateOrderRequest request) {
        orderValidator.validate(request);
        List<ResolvedOrderLine> lines = orderLineItemResolver.resolve(request.items());
        String customerType = request.customerType() == null ? "regular" : request.customerType();
        BigDecimal total = pricingCalculator.calculateOrderTotal(toLineItems(lines), customerType, request.couponCode());
        Order order = orderPersistenceService.save(request, lines);
        orderConfirmationNotifier.sendConfirmation(request.customerFullName(), order.getId(), total);
        return order;
    }

    private static List<PricingCalculator.LineItem> toLineItems(List<ResolvedOrderLine> lines) {
        return lines.stream()
                .map(line -> new PricingCalculator.LineItem(line.product().getPrice(), line.quantity()))
                .toList();
    }
}
