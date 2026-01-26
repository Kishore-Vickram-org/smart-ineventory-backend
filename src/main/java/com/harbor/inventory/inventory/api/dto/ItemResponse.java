package com.harbor.inventory.inventory.api.dto;

public record ItemResponse(
        long id,
        String sku,
        String name,
        String description,
        long quantity,
        String unit,
        LocationResponse location
) {
}
