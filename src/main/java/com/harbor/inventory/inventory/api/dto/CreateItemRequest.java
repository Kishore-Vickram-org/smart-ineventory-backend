package com.harbor.inventory.inventory.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateItemRequest(
        @NotBlank String sku,
        @NotBlank String name,
        String description,
        Long locationId
) {
}
