package org.chat.supportpilot.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="orders")
public class Order {

    @Id
    private String id;
    private String userId;
    private String status;
    private BigDecimal total;
    private LocalDate placedOn;

    public Order(){}

    public Order(String id, String userId, String status, BigDecimal total, LocalDate placedOn) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.total = total;
        this.placedOn = placedOn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDate getPlacedOn() {
        return placedOn;
    }

    public void setPlacedOn(LocalDate placedOn) {
        this.placedOn = placedOn;
    }
}
