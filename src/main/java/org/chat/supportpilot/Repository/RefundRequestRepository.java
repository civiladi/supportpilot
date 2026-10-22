package org.chat.supportpilot.Repository;

import org.chat.supportpilot.Entity.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RefundRequestRepository extends JpaRepository<RefundRequest,String> {
    Optional<RefundRequest> findByIdAndUserId(String id, String userId);
    boolean existsByOrderIdAndStatusIn(String orderId, Collection<RefundRequest.Status> statuses);
    List<RefundRequest> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, RefundRequest.Status status);
}
