package com.harbor.inventory.inventory.api.dto;

public record UpdateItemRequest(
        String name,
        String description,
        String unit,
        Long locationId
) {
}
