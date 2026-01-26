package com.harbor.inventory.inventory.api.dto;

import com.harbor.inventory.inventory.domain.MovementType;

import java.time.Instant;

public record MovementResponse(
        long id,
        long itemId,
        MovementType type,
        long quantity,
        Long fromLocationId,
        Long toLocationId,
        String note,
        Instant occurredAt
) {
}
