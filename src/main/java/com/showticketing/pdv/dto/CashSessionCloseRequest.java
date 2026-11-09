package com.showticketing.pdv.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CashSessionCloseRequest {

    @NotNull(message = "O valor contado em dinheiro é obrigatório")
    private BigDecimal closingCashReported;

    private String closingNotes;

    public CashSessionCloseRequest() {}

    public CashSessionCloseRequest(BigDecimal closingCashReported, String closingNotes) {
        this.closingCashReported = closingCashReported;
        this.closingNotes = closingNotes;
    }

    public BigDecimal getClosingCashReported() { return closingCashReported; }
    public void setClosingCashReported(BigDecimal closingCashReported) { this.closingCashReported = closingCashReported; }

    public String getClosingNotes() { return closingNotes; }
    public void setClosingNotes(String closingNotes) { this.closingNotes = closingNotes; }
}
