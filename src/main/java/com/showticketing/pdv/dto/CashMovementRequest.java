package com.showticketing.pdv.dto;

import com.showticketing.pdv.enums.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class CashMovementRequest {

    @NotNull(message = "O tipo do movimento é obrigatório")
    private MovementType type;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser positivo")
    private BigDecimal amount;

    private String reason;

    public CashMovementRequest() {}

    public CashMovementRequest(MovementType type, BigDecimal amount, String reason) {
        this.type = type;
        this.amount = amount;
        this.reason = reason;
    }

    public MovementType getType() { return type; }
    public void setType(MovementType type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
