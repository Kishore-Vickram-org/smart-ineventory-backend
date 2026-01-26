package com.harbor.inventory.inventory.api.dto;

import com.harbor.inventory.inventory.domain.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLocationRequest(
        @NotBlank String code,
        @NotBlank String name,
        @NotNull LocationType type
) {
}
