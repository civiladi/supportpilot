package org.chat.supportpilot;

import org.chat.supportpilot.Entity.Order;
import org.chat.supportpilot.Repository.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class SampleData implements CommandLineRunner {
    private final OrderRepository order;
    public SampleData(OrderRepository order){
        this.order = order;
    }

    @Override
    public void run(String... args) throws Exception {
        if(order.count()>0){
            return;
        }
        LocalDate today = LocalDate.now();
        order.saveAll(List.of(
                new Order("ORD-1001", "user-1", "DELIVERED", new BigDecimal("49.99"), today.minusDays(10)),
                new Order("ORD-1002", "user-1", "SHIPPED", new BigDecimal("120.00"), today.minusDays(3)),
                new Order("ORD-1003", "user-2", "PROCESSING", new BigDecimal("25.50"), today.minusDays(1)),
                new Order("ORD-1004", "user-2", "DELIVERED", new BigDecimal("89.00"), today.minusDays(45))
        ));
    }
}
