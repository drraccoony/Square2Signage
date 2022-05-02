package org.tyto.square.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tyto.square.client.SquareRestClient;
import org.tyto.square.client.model.app.SelectedInventoryItem;
import org.tyto.square.client.model.square.Catalog;
import org.tyto.square.client.model.square.CatalogItem;
import org.tyto.square.client.model.square.Inventory;
import org.tyto.square.client.model.square.InventoryItem;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final SquareRestClient client;
    private Map<String, SelectedInventoryItem> selectedInventory;

    @Autowired
    public InventoryService(SquareRestClient client) {
        this.client = client;
    }

    private void updateSelectedInventory(Inventory inventory) {
        inventory
                .getCounts()
                .forEach(inventoryItem -> {
                    if (selectedInventory.containsKey(inventoryItem.getCatalogObjectId())) {
                        selectedInventory
                                .get(inventoryItem.getCatalogObjectId())
                                .setQuantity(inventoryItem.getQuantity());
                    }
                });
    }

    public Mono<Catalog> getCatalogItems() {
        return client.listCatalog();
    }

    public Mono<List<SelectedInventoryItem>> initializeSelectedInventory(List<CatalogItem> selectedItems) {
        this.selectedInventory = new HashMap<>();
        selectedItems.forEach(catalogItem ->
                this.selectedInventory.put(catalogItem.getCatalogId(), new SelectedInventoryItem(catalogItem))
        );

        List<String> selectedItemIds = selectedItems.stream().map(CatalogItem::getCatalogId).collect(Collectors.toUnmodifiableList());
        return client.batchRetrieveInventoryCount(selectedItemIds)
                .map(inventory -> {
                    updateSelectedInventory(inventory);
                    return List.copyOf(selectedInventory.values());
                });
    }

    public List<SelectedInventoryItem> getSelectedInventory() {
        if (selectedInventory != null) {
            return List.copyOf(selectedInventory.values());
        } else {
            return List.of();
        }
    }

    public boolean deleteSelectedInventory() {
        selectedInventory = null;
        return true;
    }

    public Mono<List<SelectedInventoryItem>> refreshInventoryCount() {
        if (selectedInventory != null) {
            List<String> selectedItemIds = new LinkedList<>(selectedInventory.keySet());
            return client.batchRetrieveInventoryCount(selectedItemIds)
                    .map(inventory -> {
                        updateSelectedInventory(inventory);
                        Set<String> inStockItems = inventory.getCounts().stream()
                                .map(InventoryItem::getCatalogObjectId)
                                .collect(Collectors.toSet());
                        Set<String> outOfStockItems = new HashSet<>();
                        selectedInventory.forEach((key, value) -> {
                            if (!inStockItems.contains(key)) {
                                outOfStockItems.add(key);
                            }
                        });
                        outOfStockItems.forEach(itemId -> selectedInventory.get(itemId).setQuantity("0"));
                        return new ArrayList<>(selectedInventory.values());
                    });
        } else {
            return Mono.just(List.of());
        }
    }
}
