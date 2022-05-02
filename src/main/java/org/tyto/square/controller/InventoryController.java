package org.tyto.square.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tyto.square.client.model.app.SelectedInventoryItem;
import org.tyto.square.client.model.square.Catalog;
import org.tyto.square.client.model.square.CatalogItem;
import org.tyto.square.service.InventoryService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/catalog")
    public Mono<Catalog> getCatalog() {
        return inventoryService.getCatalogItems();
    }

    @PostMapping
    public Mono<List<SelectedInventoryItem>> setSelectedInventory(@RequestBody List<CatalogItem> selectedItems) {
        return inventoryService.initializeSelectedInventory(selectedItems);
    }

    @GetMapping
    public List<SelectedInventoryItem> getSelectedInventory() {
        return inventoryService.getSelectedInventory();
    }

    @DeleteMapping
    public String removeSelectedInventory() {
        return inventoryService.deleteSelectedInventory() ? "Selected inventory deleted" : "Failed to delete selected inventory";
    }

    @GetMapping("/refresh")
    public Mono<List<SelectedInventoryItem>> refreshCounts() {
        return inventoryService.refreshInventoryCount();
    }
}
