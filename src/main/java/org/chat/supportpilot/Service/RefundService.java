package org.chat.supportpilot.Service;

import org.chat.supportpilot.Entity.RefundRequest;
import org.chat.supportpilot.Repository.OrderRepository;
import org.chat.supportpilot.Repository.RefundRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class RefundService {
    private static final int refund_days = 7;
    private final OrderRepository orderRepository;
    private final RefundRequestRepository refund;

    public RefundService(OrderRepository orderRepository, RefundRequestRepository refund){
        this.orderRepository = orderRepository;this.refund = refund;
    }

    @Transactional
    public String requestRefund(String orderId, String userId, String reason){
        var found = orderRepository.findByIdAndUserId(orderId,userId);
        if(found.isEmpty()){
            return "No order with ID "+orderId+" found for user "+userId;
        }
        var order = found.get();
        String status = order.getStatus();
        if(!"DELIVERED".equals(status)){
            return "Order "+orderId+" is not eligible for refund yet, since it has not been delivered yet.";
        }
        if(order.getPlacedOn().isBefore(LocalDate.now().minusDays(refund_days))){
            return "Order "+orderId+" is outside the refund window days";
        }
        if(refund.existsByOrderIdAndStatusIn(orderId, List.of(RefundRequest.Status.PENDING, RefundRequest.Status.CONFIRMED))){
            return "A refund for order "+orderId+" is already either pending or confirmed";
        }
        RefundRequest req = new RefundRequest(orderId,userId,reason);
        refund.save(req);
        return "Refund request was created, tell the customer to confirm it.";
    }

    @Transactional
    public RefundRequest confirm(String userId, String refundId){
        var req = refund.findByIdAndUserId(refundId,userId);
        if(req.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refund Request not found");
        }
        RefundRequest request = req.get();
        if(request.getStatus()!=RefundRequest.Status.PENDING){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refund Request is already confirmed");
        }
        request.confirm();
        return request;
    }
}
