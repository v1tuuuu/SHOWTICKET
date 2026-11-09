package com.showticketing.pdv.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "batches")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String batchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_internal_id", nullable = false)
    @JsonBackReference
    private Sector sector;

    @Column(nullable = false)
    private Integer batchNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private Integer allocatedQuantity;

    @Column(nullable = false)
    private Integer soldQuantity = 0;

    @Column(nullable = false)
    private boolean active = true;

    public Batch() {}

    public Batch(String batchId, Integer batchNumber, BigDecimal basePrice, Integer allocatedQuantity) {
        this.batchId = batchId;
        this.batchNumber = batchNumber;
        this.basePrice = basePrice;
        this.allocatedQuantity = allocatedQuantity;
        this.soldQuantity = 0;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public Sector getSector() { return sector; }
    public void setSector(Sector sector) { this.sector = sector; }

    public Integer getBatchNumber() { return batchNumber; }
    public void setBatchNumber(Integer batchNumber) { this.batchNumber = batchNumber; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public Integer getAllocatedQuantity() { return allocatedQuantity; }
    public void setAllocatedQuantity(Integer allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }

    public Integer getSoldQuantity() { return soldQuantity; }
    public void setSoldQuantity(Integer soldQuantity) { this.soldQuantity = soldQuantity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getAvailableQuantity() {
        return Math.max(0, allocatedQuantity - soldQuantity);
    }
}
