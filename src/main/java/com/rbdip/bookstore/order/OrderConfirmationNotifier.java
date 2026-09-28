package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmationNotifier {

    public void sendConfirmation(String customerName, Long orderId, BigDecimal total) {
        // Реальный почтовый транспорт не настроен в учебном проекте - здесь
        // просто эмулируется побочный эффект отправки письма.
        System.out.printf(
                "[email] Dear %s, your order #%d for %s has been placed.%n", customerName, orderId, total);
    }
}
