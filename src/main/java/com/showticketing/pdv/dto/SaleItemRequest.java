package com.showticketing.pdv.dto;

import com.showticketing.pdv.enums.TicketType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SaleItemRequest {

    @NotBlank(message = "O ID do evento é obrigatório")
    private String eventId;

    @NotBlank(message = "O ID do setor é obrigatório")
    private String sectorId;

    @NotBlank(message = "O ID do lote é obrigatório")
    private String batchId;

    @NotNull(message = "O tipo do ingresso é obrigatório")
    private TicketType ticketType;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade mínima é 1")
    private Integer quantity;

    private String holderName;
    private String documentNumber;

    public SaleItemRequest() {}

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getSectorId() { return sectorId; }
    public void setSectorId(String sectorId) { this.sectorId = sectorId; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public TicketType getTicketType() { return ticketType; }
    public void setTicketType(TicketType ticketType) { this.ticketType = ticketType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
}
