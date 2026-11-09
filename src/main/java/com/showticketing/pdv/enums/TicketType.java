package com.showticketing.pdv.enums;

import java.math.BigDecimal;

public enum TicketType {
    INTEIRA(BigDecimal.valueOf(1.0), "Inteira"),
    MEIA_ESTUDANTE(BigDecimal.valueOf(0.5), "Meia Estudante"),
    MEIA_IDOSO(BigDecimal.valueOf(0.5), "Meia Idoso"),
    MEIA_PCD(BigDecimal.valueOf(0.5), "Meia PCD"),
    MEIA_PROFESSOR(BigDecimal.valueOf(0.5), "Meia Professor"),
    SOCIAL(BigDecimal.valueOf(0.6), "Social (+ 1kg de Alimento)"),
    CORTESIA(BigDecimal.ZERO, "Cortesia");

    private final BigDecimal multiplier;
    private final String description;

    TicketType(BigDecimal multiplier, String description) {
        this.multiplier = multiplier;
        this.description = description;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresDocument() {
        return this != INTEIRA && this != CORTESIA;
    }
}
