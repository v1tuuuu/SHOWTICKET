package com.showticketing.pdv.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckInRequest {

    @NotBlank(message = "O código ou hash do ingresso é obrigatório")
    private String ticketIdentifier;

    private String gate;
    private String operator;

    public CheckInRequest() {}

    public CheckInRequest(String ticketIdentifier, String gate, String operator) {
        this.ticketIdentifier = ticketIdentifier;
        this.gate = gate;
        this.operator = operator;
    }

    public String getTicketIdentifier() { return ticketIdentifier; }
    public void setTicketIdentifier(String ticketIdentifier) { this.ticketIdentifier = ticketIdentifier; }

    public String getGate() { return gate; }
    public void setGate(String gate) { this.gate = gate; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
}
