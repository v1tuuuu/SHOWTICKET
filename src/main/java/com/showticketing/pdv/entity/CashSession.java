package com.showticketing.pdv.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.showticketing.pdv.enums.CashSessionStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cash_sessions")
public class CashSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @Column(nullable = false)
    private String posTerminalId;

    @Column(nullable = false)
    private String operatorId;

    @Column(nullable = false)
    private String operatorName;

    @Column(nullable = false)
    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal initialFloat = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CashSessionStatus status = CashSessionStatus.OPEN;

    private BigDecimal closingCashReported;
    private String closingNotes;

    @OneToMany(mappedBy = "cashSession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<CashMovement> movements = new ArrayList<>();

    @OneToMany(mappedBy = "cashSession", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SaleOrder> sales = new ArrayList<>();

    public CashSession() {}

    public CashSession(String sessionId, String posTerminalId, String operatorId, String operatorName, BigDecimal initialFloat) {
        this.sessionId = sessionId;
        this.posTerminalId = posTerminalId;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.initialFloat = initialFloat != null ? initialFloat : BigDecimal.ZERO;
        this.openedAt = LocalDateTime.now();
        this.status = CashSessionStatus.OPEN;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getPosTerminalId() { return posTerminalId; }
    public void setPosTerminalId(String posTerminalId) { this.posTerminalId = posTerminalId; }

    public String getOperatorId() { return operatorId; }
    public void setOperatorId(String operatorId) { this.operatorId = operatorId; }

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }

    public LocalDateTime getOpenedAt() { return openedAt; }
    public void setOpenedAt(LocalDateTime openedAt) { this.openedAt = openedAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public BigDecimal getInitialFloat() { return initialFloat; }
    public void setInitialFloat(BigDecimal initialFloat) { this.initialFloat = initialFloat; }

    public CashSessionStatus getStatus() { return status; }
    public void setStatus(CashSessionStatus status) { this.status = status; }

    public BigDecimal getClosingCashReported() { return closingCashReported; }
    public void setClosingCashReported(BigDecimal closingCashReported) { this.closingCashReported = closingCashReported; }

    public String getClosingNotes() { return closingNotes; }
    public void setClosingNotes(String closingNotes) { this.closingNotes = closingNotes; }

    public List<CashMovement> getMovements() { return movements; }
    public void setMovements(List<CashMovement> movements) { this.movements = movements; }

    public List<SaleOrder> getSales() { return sales; }
    public void setSales(List<SaleOrder> sales) { this.sales = sales; }

    public void addMovement(CashMovement movement) {
        this.movements.add(movement);
        movement.setCashSession(this);
    }
}
