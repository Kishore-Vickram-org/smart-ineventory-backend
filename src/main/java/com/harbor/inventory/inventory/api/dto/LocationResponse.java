package com.harbor.inventory.inventory.api.dto;

import com.harbor.inventory.inventory.domain.LocationType;

public record LocationResponse(
        long id,
        String code,
        String name,
        LocationType type
) {
}
