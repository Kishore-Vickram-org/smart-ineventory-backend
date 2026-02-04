package com.harbor.inventory.inventory.api;

import com.harbor.inventory.inventory.api.dto.*;
import com.harbor.inventory.inventory.domain.Item;
import com.harbor.inventory.inventory.domain.StockMovement;
import com.harbor.inventory.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemsController {

    private final InventoryService inventoryService;

    public ItemsController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<ItemResponse> list() {
        return inventoryService.listItems().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable long id) {
        return toResponse(inventoryService.getItem(id));
    }

    @PostMapping
    public ItemResponse create(@Valid @RequestBody CreateItemRequest request) {
        Item item = new Item();
        item.setSku(request.sku());
        item.setName(request.name());
        item.setDescription(request.description());
        item.setQuantity(request.quantity());
        item.setUnit(request.unit());
        return toResponse(inventoryService.createItem(item, request.locationId()));
    }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable long id, @Valid @RequestBody UpdateItemRequest request) {
        Item patch = new Item();
        patch.setName(request.name());
        patch.setDescription(request.description());
        patch.setUnit(request.unit());
        return toResponse(inventoryService.updateItem(id, patch, request.locationId(), request.quantity()));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        inventoryService.deleteItem(id);
    }

    @PostMapping("/{id}/movements")
    public MovementResponse move(@PathVariable long id, @Valid @RequestBody CreateMovementRequest request) {
        StockMovement movement = inventoryService.createMovement(id, request.type(), request.quantity(), request.toLocationId(), request.note());
        return toResponse(movement);
    }

    private ItemResponse toResponse(Item item) {
        LocationResponse location = null;
        if (item.getLocation() != null) {
            location = LocationsController.toResponse(item.getLocation());
        }
        return new ItemResponse(item.getId(), item.getSku(), item.getName(), item.getDescription(), item.getQuantity(), item.getUnit(), location);
    }

    static MovementResponse toResponse(StockMovement movement) {
        Long fromId = movement.getFromLocation() == null ? null : movement.getFromLocation().getId();
        Long toId = movement.getToLocation() == null ? null : movement.getToLocation().getId();
        return new MovementResponse(
                movement.getId(),
                movement.getItem().getId(),
                movement.getType(),
                movement.getQuantity(),
                fromId,
                toId,
                movement.getNote(),
                movement.getOccurredAt()
        );
    }
}
