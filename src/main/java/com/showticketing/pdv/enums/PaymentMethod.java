package com.showticketing.pdv.enums;

public enum PaymentMethod {
    CASH("Dinheiro"),
    DEBIT_CARD("Cartão de Débito"),
    CREDIT_CARD("Cartão de Crédito"),
    PIX("PIX"),
    VOUCHER("Vale / Voucher");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
