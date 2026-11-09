package com.showticketing.pdv.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CashSessionOpenRequest {

    @NotBlank(message = "O ID do terminal é obrigatório")
    private String posTerminalId;

    @NotBlank(message = "O ID do operador é obrigatório")
    private String operatorId;

    @NotBlank(message = "O nome do operador é obrigatório")
    private String operatorName;

    @NotNull(message = "O saldo inicial (fundo de troco) é obrigatório")
    private BigDecimal initialFloat;

    public CashSessionOpenRequest() {}

    public String getPosTerminalId() { return posTerminalId; }
    public void setPosTerminalId(String posTerminalId) { this.posTerminalId = posTerminalId; }

    public String getOperatorId() { return operatorId; }
    public void setOperatorId(String operatorId) { this.operatorId = operatorId; }

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }

    public BigDecimal getInitialFloat() { return initialFloat; }
    public void setInitialFloat(BigDecimal initialFloat) { this.initialFloat = initialFloat; }
}
