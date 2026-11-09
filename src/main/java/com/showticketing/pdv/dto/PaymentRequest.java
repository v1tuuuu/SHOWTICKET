package com.showticketing.pdv.dto;

import com.showticketing.pdv.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class PaymentRequest {

    @NotNull(message = "O método de pagamento é obrigatório")
    private PaymentMethod method;

    @NotNull(message = "O valor entregue é obrigatório")
    @Positive(message = "O valor entregue deve ser positivo")
    private BigDecimal amountTendered;

    @NotNull(message = "O valor aplicado é obrigatório")
    @Positive(message = "O valor aplicado deve ser positivo")
    private BigDecimal amountApplied;

    private String pixTransactionId;

    public PaymentRequest() {}

    public PaymentRequest(PaymentMethod method, BigDecimal amountTendered, BigDecimal amountApplied, String pixTransactionId) {
        this.method = method;
        this.amountTendered = amountTendered;
        this.amountApplied = amountApplied;
        this.pixTransactionId = pixTransactionId;
    }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public BigDecimal getAmountTendered() { return amountTendered; }
    public void setAmountTendered(BigDecimal amountTendered) { this.amountTendered = amountTendered; }

    public BigDecimal getAmountApplied() { return amountApplied; }
    public void setAmountApplied(BigDecimal amountApplied) { this.amountApplied = amountApplied; }

    public String getPixTransactionId() { return pixTransactionId; }
    public void setPixTransactionId(String pixTransactionId) { this.pixTransactionId = pixTransactionId; }
}
