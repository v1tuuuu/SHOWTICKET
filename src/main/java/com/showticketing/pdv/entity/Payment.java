package com.showticketing.pdv.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.showticketing.pdv.enums.PaymentMethod;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_order_id", nullable = false)
    @JsonBackReference
    private SaleOrder saleOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountTendered;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountApplied;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal changeReturned = BigDecimal.ZERO;

    private String pixTransactionId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Payment() {}

    public Payment(PaymentMethod method, BigDecimal amountTendered, BigDecimal amountApplied, BigDecimal changeReturned, String pixTransactionId) {
        this.method = method;
        this.amountTendered = amountTendered;
        this.amountApplied = amountApplied;
        this.changeReturned = changeReturned != null ? changeReturned : BigDecimal.ZERO;
        this.pixTransactionId = pixTransactionId;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SaleOrder getSaleOrder() { return saleOrder; }
    public void setSaleOrder(SaleOrder saleOrder) { this.saleOrder = saleOrder; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public BigDecimal getAmountTendered() { return amountTendered; }
    public void setAmountTendered(BigDecimal amountTendered) { this.amountTendered = amountTendered; }

    public BigDecimal getAmountApplied() { return amountApplied; }
    public void setAmountApplied(BigDecimal amountApplied) { this.amountApplied = amountApplied; }

    public BigDecimal getChangeReturned() { return changeReturned; }
    public void setChangeReturned(BigDecimal changeReturned) { this.changeReturned = changeReturned; }

    public String getPixTransactionId() { return pixTransactionId; }
    public void setPixTransactionId(String pixTransactionId) { this.pixTransactionId = pixTransactionId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
