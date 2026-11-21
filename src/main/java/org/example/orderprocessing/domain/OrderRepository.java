package org.example.orderprocessing.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByAdvisorIdOrderByOrderDateDesc(String advisorId);

    List<Order> findByAdvisorIdAndOrderDateBetweenOrderByOrderDateDesc(String advisorId, LocalDate from, LocalDate to);

    @Query("""
            select o.advisorId as advisorId, sum(o.amount) as totalSales, count(o) as orderCount
            from Order o
            where o.orderDate between :from and :to and o.status in :statuses
            group by o.advisorId
            """)
    List<AdvisorSales> summarizeSalesByAdvisor(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("statuses") Collection<OrderStatus> statuses
    );
}
