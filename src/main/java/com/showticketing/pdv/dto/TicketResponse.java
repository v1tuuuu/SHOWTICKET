package com.showticketing.pdv.dto;

import com.showticketing.pdv.enums.TicketStatus;
import com.showticketing.pdv.enums.TicketType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TicketResponse {

    private String ticketId;
    private String ticketCode;
    private String eventName;
    private String venueName;
    private LocalDateTime eventDate;
    private String sectorName;
    private Integer batchNumber;
    private TicketType ticketType;
    private BigDecimal pricePaid;
    private String holderName;
    private String holderCpf;
    private String documentNumber;
    private String validationHash;
    private String qrCodeData;
    private String qrCodeBase64;
    private TicketStatus status;

    public TicketResponse() {}

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getTicketCode() { return ticketCode; }
    public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public String getSectorName() { return sectorName; }
    public void setSectorName(String sectorName) { this.sectorName = sectorName; }

    public Integer getBatchNumber() { return batchNumber; }
    public void setBatchNumber(Integer batchNumber) { this.batchNumber = batchNumber; }

    public TicketType getTicketType() { return ticketType; }
    public void setTicketType(TicketType ticketType) { this.ticketType = ticketType; }

    public BigDecimal getPricePaid() { return pricePaid; }
    public void setPricePaid(BigDecimal pricePaid) { this.pricePaid = pricePaid; }

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public String getHolderCpf() { return holderCpf; }
    public void setHolderCpf(String holderCpf) { this.holderCpf = holderCpf; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public String getValidationHash() { return validationHash; }
    public void setValidationHash(String validationHash) { this.validationHash = validationHash; }

    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }

    public String getQrCodeBase64() { return qrCodeBase64; }
    public void setQrCodeBase64(String qrCodeBase64) { this.qrCodeBase64 = qrCodeBase64; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
}
