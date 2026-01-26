package com.harbor.inventory.inventory.api.dto;

import com.harbor.inventory.inventory.domain.LocationType;
import jakarta.validation.constraints.NotBlank;

public record UpdateLocationRequest(
        @NotBlank String name,
        LocationType type
) {
}
