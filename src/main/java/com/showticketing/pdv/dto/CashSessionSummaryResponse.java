package com.showticketing.pdv.dto;

import com.showticketing.pdv.enums.CashSessionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CashSessionSummaryResponse {

    private String sessionId;
    private String posTerminalId;
    private String operatorId;
    private String operatorName;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private CashSessionStatus status;

    private BigDecimal initialFloat;
    private BigDecimal cashSalesTotal;
    private BigDecimal debitSalesTotal;
    private BigDecimal creditSalesTotal;
    private BigDecimal pixSalesTotal;
    private BigDecimal suppliesTotal;
    private BigDecimal bleedTotal;

    private BigDecimal expectedCashBalance;
    private BigDecimal reportedCashBalance;
    private BigDecimal cashDifference;

    private BigDecimal grossTotalSales;
    private int ticketsSoldCount;

    public CashSessionSummaryResponse() {}

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

    public CashSessionStatus getStatus() { return status; }
    public void setStatus(CashSessionStatus status) { this.status = status; }

    public BigDecimal getInitialFloat() { return initialFloat; }
    public void setInitialFloat(BigDecimal initialFloat) { this.initialFloat = initialFloat; }

    public BigDecimal getCashSalesTotal() { return cashSalesTotal; }
    public void setCashSalesTotal(BigDecimal cashSalesTotal) { this.cashSalesTotal = cashSalesTotal; }

    public BigDecimal getDebitSalesTotal() { return debitSalesTotal; }
    public void setDebitSalesTotal(BigDecimal debitSalesTotal) { this.debitSalesTotal = debitSalesTotal; }

    public BigDecimal getCreditSalesTotal() { return creditSalesTotal; }
    public void setCreditSalesTotal(BigDecimal creditSalesTotal) { this.creditSalesTotal = creditSalesTotal; }

    public BigDecimal getPixSalesTotal() { return pixSalesTotal; }
    public void setPixSalesTotal(BigDecimal pixSalesTotal) { this.pixSalesTotal = pixSalesTotal; }

    public BigDecimal getSuppliesTotal() { return suppliesTotal; }
    public void setSuppliesTotal(BigDecimal suppliesTotal) { this.suppliesTotal = suppliesTotal; }

    public BigDecimal getBleedTotal() { return bleedTotal; }
    public void setBleedTotal(BigDecimal bleedTotal) { this.bleedTotal = bleedTotal; }

    public BigDecimal getExpectedCashBalance() { return expectedCashBalance; }
    public void setExpectedCashBalance(BigDecimal expectedCashBalance) { this.expectedCashBalance = expectedCashBalance; }

    public BigDecimal getReportedCashBalance() { return reportedCashBalance; }
    public void setReportedCashBalance(BigDecimal reportedCashBalance) { this.reportedCashBalance = reportedCashBalance; }

    public BigDecimal getCashDifference() { return cashDifference; }
    public void setCashDifference(BigDecimal cashDifference) { this.cashDifference = cashDifference; }

    public BigDecimal getGrossTotalSales() { return grossTotalSales; }
    public void setGrossTotalSales(BigDecimal grossTotalSales) { this.grossTotalSales = grossTotalSales; }

    public int getTicketsSoldCount() { return ticketsSoldCount; }
    public void setTicketsSoldCount(int ticketsSoldCount) { this.ticketsSoldCount = ticketsSoldCount; }
}
