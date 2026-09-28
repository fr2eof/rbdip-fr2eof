package com.rbdip.bookstore.order;

import com.rbdip.bookstore.product.Product;

public record ResolvedOrderLine(Product product, int quantity) {
}
