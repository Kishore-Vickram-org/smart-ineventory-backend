package com.harbor.inventory.inventory.api;

import com.harbor.inventory.inventory.api.dto.CreateLocationRequest;
import com.harbor.inventory.inventory.api.dto.LocationResponse;
import com.harbor.inventory.inventory.api.dto.UpdateLocationRequest;
import com.harbor.inventory.inventory.domain.Location;
import com.harbor.inventory.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationsController {

    private final InventoryService inventoryService;

    public LocationsController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<LocationResponse> list() {
        return inventoryService.listLocations().stream().map(LocationsController::toResponse).toList();
    }

    @PostMapping
    public LocationResponse create(@Valid @RequestBody CreateLocationRequest request) {
        Location location = new Location();
        location.setCode(request.code());
        location.setName(request.name());
        location.setType(request.type());
        return toResponse(inventoryService.createLocation(location));
    }

    @PutMapping("/{id}")
    public LocationResponse update(@PathVariable long id, @Valid @RequestBody UpdateLocationRequest request) {
        Location patch = new Location();
        patch.setName(request.name());
        patch.setType(request.type());
        return toResponse(inventoryService.updateLocation(id, patch));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        inventoryService.deleteLocation(id);
    }

    static LocationResponse toResponse(Location location) {
        return new LocationResponse(location.getId(), location.getCode(), location.getName(), location.getType());
    }
}
