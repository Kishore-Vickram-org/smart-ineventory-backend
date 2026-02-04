package com.harbor.inventory.inventory.service;

import com.harbor.inventory.inventory.domain.*;
import com.harbor.inventory.inventory.repo.ItemRepository;
import com.harbor.inventory.inventory.repo.LocationRepository;
import com.harbor.inventory.inventory.repo.StockMovementRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;
    private final StockMovementRepository stockMovementRepository;

    public InventoryService(ItemRepository itemRepository,
                            LocationRepository locationRepository,
                            StockMovementRepository stockMovementRepository) {
        this.itemRepository = itemRepository;
        this.locationRepository = locationRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    // Locations
    public List<Location> listLocations() {
        return locationRepository.findAll();
    }

    public Location getLocation(long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location not found: " + id));
    }

    @Transactional
    public Location createLocation(Location location) {
        if (location.getCode() == null || location.getCode().isBlank()) {
            throw new BadRequestException("Location code is required");
        }
        if (locationRepository.findByCode(location.getCode()).isPresent()) {
            throw new BadRequestException("Location code already exists: " + location.getCode());
        }
        return locationRepository.save(location);
    }

    @Transactional
    public Location updateLocation(long id, Location patch) {
        Location existing = getLocation(id);
        if (patch.getName() != null) {
            existing.setName(patch.getName());
        }
        if (patch.getType() != null) {
            existing.setType(patch.getType());
        }
        return locationRepository.save(existing);
    }

    @Transactional
    public void deleteLocation(long id) {
        Location location = getLocation(id);
        locationRepository.delete(location);
    }

    // Items
    public List<Item> listItems() {
        return itemRepository.findAll();
    }

    public Item getItem(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));
    }

    @Transactional
    public Item createItem(Item item, Long locationId) {
        if (item.getSku() == null || item.getSku().isBlank()) {
            throw new BadRequestException("SKU is required");
        }
        if (itemRepository.findBySku(item.getSku()).isPresent()) {
            throw new BadRequestException("SKU already exists: " + item.getSku());
        }
        if (item.getQuantity() < 0) {
            throw new BadRequestException("Quantity cannot be negative");
        }
        if (locationId != null) {
            item.setLocation(getLocation(locationId));
        }
        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(long id, Item patch, Long locationId, Long quantity) {
        Item existing = getItem(id);
        if (patch.getName() != null) {
            existing.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            existing.setDescription(patch.getDescription());
        }
        if (quantity != null) {
            if (quantity < 0) {
                throw new BadRequestException("Quantity cannot be negative");
            }
            existing.setQuantity(quantity);
        }
        if (patch.getUnit() != null) {
            existing.setUnit(patch.getUnit());
        }
        if (locationId != null) {
            existing.setLocation(getLocation(locationId));
        }
        return itemRepository.save(existing);
    }

    @Transactional
    public void deleteItem(long id) {
        Item existing = getItem(id);
		// Prevent FK constraint violations if movements reference this item.
		stockMovementRepository.deleteByItemId(id);
        itemRepository.delete(existing);
    }

    // Movements
    @Transactional
    public StockMovement createMovement(long itemId, MovementType type, long quantity, Long toLocationId, String note) {
        Item item = getItem(itemId);
        if (type == null) {
            throw new BadRequestException("Movement type is required");
        }
        if (type == MovementType.ADJUST) {
            if (quantity == 0) {
                throw new BadRequestException("Adjust quantity cannot be 0");
            }
        } else {
            if (quantity <= 0) {
                throw new BadRequestException("Quantity must be > 0");
            }
        }

        Location fromLocation = item.getLocation();
        Location toLocation = null;
        if (toLocationId != null) {
            toLocation = getLocation(toLocationId);
            item.setLocation(toLocation);
        }

        long newQty;
        switch (type) {
            case IN -> newQty = item.getQuantity() + quantity;
            case OUT -> newQty = item.getQuantity() - quantity;
            case ADJUST -> newQty = item.getQuantity() + quantity;
            default -> throw new BadRequestException("Unsupported movement type: " + type);
        }

        if (newQty < 0) {
            throw new BadRequestException("Insufficient stock (would go negative)");
        }

        item.setQuantity(newQty);
        itemRepository.save(item);

        StockMovement movement = new StockMovement();
        movement.setItem(item);
        movement.setType(type);
        movement.setQuantity(quantity);
        movement.setFromLocation(fromLocation);
        movement.setToLocation(toLocation);
        movement.setNote(note);

        return stockMovementRepository.save(movement);
    }

    public List<StockMovement> listMovements(Long itemId, MovementType type, Long locationId, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 500);
        PageRequest page = PageRequest.of(0, safeLimit);
        return stockMovementRepository.search(itemId, type, locationId, page);
    }
}
