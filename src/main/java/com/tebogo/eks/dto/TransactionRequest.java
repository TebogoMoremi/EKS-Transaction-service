package com.tebogo.eks.dto;

import java.math.BigDecimal;

public record TransactionRequest(
    TransactionType type,
    BigDecimal amount,
    String currency,
    PartyRequest initiator,
    PartyRequest receiver,
    LocationRequest location,
    String sourceSystem,
    String destinationType
) {
}