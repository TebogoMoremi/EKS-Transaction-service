package com.tebogo.eks.error;

public record ErrorResponse(
        String timestamp,
        String correlationId,
        String code,
        String message
) {
}