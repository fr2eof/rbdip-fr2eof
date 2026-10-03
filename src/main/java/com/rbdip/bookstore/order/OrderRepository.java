package com.rbdip.bookstore.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            select distinct o from Order o
            left join fetch o.customer
            left join fetch o.items items
            left join fetch items.product
            """)
    List<Order> findAllWithItems();
}
