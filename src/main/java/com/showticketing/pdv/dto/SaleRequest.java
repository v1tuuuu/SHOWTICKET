package com.showticketing.pdv.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class SaleRequest {

    private String sessionId;
    private String posTerminalId;
    private String operatorName;

    private String customerName;
    private String customerCpf;
    private String customerEmail;

    private BigDecimal discount = BigDecimal.ZERO;

    @NotEmpty(message = "A venda deve conter ao menos um ingresso")
    @Valid
    private List<SaleItemRequest> items;

    @NotEmpty(message = "Ao menos uma forma de pagamento deve ser informada")
    @Valid
    private List<PaymentRequest> payments;

    public SaleRequest() {}

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getPosTerminalId() { return posTerminalId; }
    public void setPosTerminalId(String posTerminalId) { this.posTerminalId = posTerminalId; }

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerCpf() { return customerCpf; }
    public void setCustomerCpf(String customerCpf) { this.customerCpf = customerCpf; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public List<SaleItemRequest> getItems() { return items; }
    public void setItems(List<SaleItemRequest> items) { this.items = items; }

    public List<PaymentRequest> getPayments() { return payments; }
    public void setPayments(List<PaymentRequest> payments) { this.payments = payments; }
}
