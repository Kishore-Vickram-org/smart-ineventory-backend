package com.harbor.inventory.inventory.api;

import com.harbor.inventory.inventory.api.dto.MovementResponse;
import com.harbor.inventory.inventory.domain.MovementType;
import com.harbor.inventory.inventory.domain.StockMovement;
import com.harbor.inventory.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
public class MovementsController {

    private final InventoryService inventoryService;

    public MovementsController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<MovementResponse> list(@RequestParam(required = false) Long itemId,
                                      @RequestParam(required = false) MovementType type,
                                      @RequestParam(required = false) Long locationId,
                                      @RequestParam(defaultValue = "100") int limit) {
        List<StockMovement> movements = inventoryService.listMovements(itemId, type, locationId, limit);
        return movements.stream().map(ItemsController::toResponse).toList();
    }
}
