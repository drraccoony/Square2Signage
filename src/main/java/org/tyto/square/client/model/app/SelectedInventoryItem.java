package org.tyto.square.client.model.app;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.tyto.square.client.model.square.CatalogItem;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SelectedInventoryItem {
    private CatalogItem catalogItem;
    private String quantity;

    public SelectedInventoryItem() {
        this(null, "0");
    }

    public SelectedInventoryItem(CatalogItem catalogItem) {
        this(catalogItem, "0");
    }

    public SelectedInventoryItem(CatalogItem catalogItem, String quantity) {
        this.catalogItem = catalogItem;
        this.quantity = quantity;
    }
}
