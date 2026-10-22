package org.chat.supportpilot.Controller;

import org.chat.supportpilot.Auth;
import org.chat.supportpilot.Entity.RefundRequest;
import org.chat.supportpilot.Repository.RefundRequestRepository;
import org.chat.supportpilot.Service.RefundService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    public record Refund(String id, String orderId, String status, String reason, Instant createdAt){
        static Refund of(RefundRequest r){
            return new Refund(r.getId(), r.getOrderId(), r.getStatus().name(), r.getReason(), r.getCreatedAt());
        }
    }

    private final RefundService refundService;
    private final RefundRequestRepository refundRequestRepository;

    public RefundController(RefundService refundService, RefundRequestRepository refundRequestRepository) {
        this.refundService = refundService;
        this.refundRequestRepository = refundRequestRepository;
    }

    @GetMapping("/pending")
    public List<Refund> pending(@RequestHeader("X-User-Id") String userId){
        Auth.checkValid(userId);
        return refundRequestRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId,RefundRequest.Status.PENDING)
                .stream().map(Refund::of).toList();
    }

    @PostMapping("/{id}/confirm")
    public Refund confirm(@PathVariable String id, @RequestHeader("X-User-Id") String userId){
        Auth.checkValid(userId);
        if(!id.matches("^[0-9a-fA-F-]{36}$")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid refund id");
        }
        return Refund.of(refundService.confirm(userId, id));
    }
}
