package com.harbor.inventory.inventory.api.dto;

import com.harbor.inventory.inventory.domain.MovementType;
import jakarta.validation.constraints.NotNull;

public record CreateMovementRequest(
        @NotNull MovementType type,
        long quantity,
        Long toLocationId,
        String note
) {
}
