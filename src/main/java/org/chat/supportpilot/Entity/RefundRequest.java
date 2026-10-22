package org.chat.supportpilot.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refund_requests")
public class RefundRequest {

    public enum Status{PENDING, CONFIRMED}

    @Id
    private String id;
    private String orderId;
    private String userId;
    private String reason;
    private Status status;
    private Instant createdAt;
    private Instant decidedAt;
    @Version
    private Long version;

    protected RefundRequest(){}

    public RefundRequest(String orderId, String userId, String reason) {
        this.orderId = orderId;
        this.userId = userId;
        this.reason = reason;
        this.id= UUID.randomUUID().toString();
        this.status=Status.PENDING;
        this.createdAt=Instant.now();
    }
    public void confirm(){
        this.status=Status.CONFIRMED;this.decidedAt=Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
