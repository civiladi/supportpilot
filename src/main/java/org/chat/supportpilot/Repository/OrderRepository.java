package org.chat.supportpilot.Repository;

import org.chat.supportpilot.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByIdAndUserId(String id, String userId);
}
