package com.harbor.inventory.inventory.service;

import com.harbor.inventory.inventory.domain.*;
import com.harbor.inventory.inventory.repo.ItemRepository;
import com.harbor.inventory.inventory.repo.LocationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Bean
    CommandLineRunner seed(LocationRepository locationRepository, ItemRepository itemRepository) {
        return args -> {
            if (!seedEnabled) {
                return;
            }

            if (locationRepository.count() == 0) {
                Location dock = new Location();
                dock.setCode("DOCK-A");
                dock.setName("Dock A");
                dock.setType(LocationType.DOCK);
                locationRepository.save(dock);

                Location wh = new Location();
                wh.setCode("WH-01");
                wh.setName("Warehouse 01");
                wh.setType(LocationType.WAREHOUSE);
                locationRepository.save(wh);
            }
        };
    }
}
