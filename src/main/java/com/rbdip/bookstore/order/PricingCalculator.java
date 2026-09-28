package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PricingCalculator {

    private static final int BULK_DISCOUNT_THRESHOLD = 10;
    private static final BigDecimal BULK_DISCOUNT_MULTIPLIER = new BigDecimal("0.95");
    private static final BigDecimal VIP_MULTIPLIER = new BigDecimal("0.9");
    private static final BigDecimal WHOLESALE_MULTIPLIER = new BigDecimal("0.85");
    private static final String COUPON_SAVE10 = "SAVE10";
    private static final String COUPON_SAVE20_PERCENT = "SAVE20PERCENT";
    private static final BigDecimal COUPON_PERCENT_MULTIPLIER = new BigDecimal("0.8");
    private static final BigDecimal LARGE_ORDER_THRESHOLD = new BigDecimal("1000");
    private static final BigDecimal LARGE_ORDER_MULTIPLIER = new BigDecimal("0.98");
    private static final int MONEY_SCALE = 2;
    private static final String CUSTOMER_TYPE_VIP = "vip";
    private static final String CUSTOMER_TYPE_WHOLESALE = "wholesale";

    public record LineItem(BigDecimal price, int quantity) {
    }

    public BigDecimal calculateOrderTotal(List<LineItem> items, String customerType, String couponCode) {
        BigDecimal total = calculateSubtotal(items);
        total = applyCustomerTypeDiscount(total, customerType);
        total = applyCouponDiscount(total, couponCode);
        total = applyMinimumTotal(total);
        total = applyLargeOrderDiscount(total);
        return total.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSubtotal(List<LineItem> items) {
        BigDecimal total = BigDecimal.ZERO;

        for (LineItem item : items) {
            BigDecimal linePrice = item.price().multiply(BigDecimal.valueOf(item.quantity()));
            if (item.quantity() > BULK_DISCOUNT_THRESHOLD) {
                linePrice = linePrice.multiply(BULK_DISCOUNT_MULTIPLIER);
            }
            total = total.add(linePrice);
        }
        return total;
    }

    private BigDecimal applyCustomerTypeDiscount(BigDecimal total, String customerType) {
        if (CUSTOMER_TYPE_VIP.equals(customerType)) {
            return total.multiply(VIP_MULTIPLIER);
        }
        if (CUSTOMER_TYPE_WHOLESALE.equals(customerType)) {
            return total.multiply(WHOLESALE_MULTIPLIER);
        }
        return total;
    }

    private BigDecimal applyCouponDiscount(BigDecimal total, String couponCode) {
        if (COUPON_SAVE10.equals(couponCode)) {
            return total.subtract(BigDecimal.TEN);
        }
        if (COUPON_SAVE20_PERCENT.equals(couponCode)) {
            return total.multiply(COUPON_PERCENT_MULTIPLIER);
        }
        return total;
    }

    private BigDecimal applyMinimumTotal(BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return total;
    }

    private BigDecimal applyLargeOrderDiscount(BigDecimal total) {
        if (total.compareTo(LARGE_ORDER_THRESHOLD) > 0) {
            return total.multiply(LARGE_ORDER_MULTIPLIER);
        }
        return total;
    }
}
