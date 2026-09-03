package com.tebogo.eks.error;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        String correlationId,
        String code,
        String message
) {
}