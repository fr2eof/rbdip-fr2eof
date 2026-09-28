package com.rbdip.bookstore.order;

import com.rbdip.bookstore.product.Product;
import com.rbdip.bookstore.product.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderLineItemResolver {

    private final ProductRepository productRepository;

    public OrderLineItemResolver(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ResolvedOrderLine> resolve(List<CreateOrderRequest.Item> items) {
        List<ResolvedOrderLine> lines = new ArrayList<>();
        for (CreateOrderRequest.Item raw : items) {
            Product product = productRepository.findById(raw.productId())
                    .orElseThrow(() -> new IllegalArgumentException("product " + raw.productId() + " not found"));
            int quantity = raw.quantity() == null ? 1 : raw.quantity();
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be positive");
            }
            lines.add(new ResolvedOrderLine(product, quantity));
        }
        return lines;
    }
}
