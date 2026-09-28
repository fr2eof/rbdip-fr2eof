package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.order.PricingCalculator.LineItem;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PricingCalculatorCharacterizationTest {

    private final PricingCalculator calculator = new PricingCalculator();

    @Test
    void appliesVipDiscount() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "vip", null);

        assertThat(total).isEqualByComparingTo("90.00");
    }

    @Test
    void appliesWholesaleDiscount() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "wholesale", null);

        assertThat(total).isEqualByComparingTo("85.00");
    }

    @Test
    void appliesSave10Coupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("50.00"), 1)), "regular", "SAVE10");

        assertThat(total).isEqualByComparingTo("40.00");
    }

    @Test
    void appliesSave20PercentCoupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "regular", "SAVE20PERCENT");

        assertThat(total).isEqualByComparingTo("80.00");
    }

    @Test
    void appliesBulkDiscountWhenQuantityAboveThreshold() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.00"), 11)), "regular", null);

        assertThat(total).isEqualByComparingTo("104.50");
    }

    @Test
    void doesNotApplyBulkDiscountAtThreshold() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.00"), 10)), "regular", null);

        assertThat(total).isEqualByComparingTo("100.00");
    }

    @Test
    void floorsNegativeTotalAtZero() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("5.00"), 1)), "regular", "SAVE10");

        assertThat(total).isEqualByComparingTo("0.00");
    }

    @Test
    void appliesLargeOrderDiscount() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("1100.00"), 1)), "regular", null);

        assertThat(total).isEqualByComparingTo("1078.00");
    }

    @Test
    void doesNotApplyLargeOrderDiscountAtThreshold() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("1000.00"), 1)), "regular", null);

        assertThat(total).isEqualByComparingTo("1000.00");
    }

    @Test
    void returnsZeroForEmptyItemList() {
        BigDecimal total = calculator.calculateOrderTotal(List.of(), "regular", null);

        assertThat(total).isEqualByComparingTo("0.00");
    }

    @Test
    void ignoresUnknownCustomerTypeAndCoupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("20.00"), 1)), "gold", "INVALID");

        assertThat(total).isEqualByComparingTo("20.00");
    }

    @Test
    void stacksBulkVipAndCouponDiscounts() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.00"), 11)), "vip", "SAVE10");

        assertThat(total).isEqualByComparingTo("84.05");
    }
}
