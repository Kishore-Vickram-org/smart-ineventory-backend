package com.harbor.inventory.inventory.api.dto;

import jakarta.validation.constraints.Min;

public record UpdateItemRequest(
        String name,
        String description,
        @Min(0) Long quantity,
        String unit,
        Long locationId
) {
}
