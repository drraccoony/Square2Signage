package org.tyto.square.client.model.square;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class InventoryDeserializer extends StdDeserializer<Inventory> {

    private static final String FIELD_COUNTS = "counts";
    private static final String FIELD_CATALOG_OBJECT_ID = "catalog_object_id";
    private static final String FIELD_QUANTITY = "quantity";

    public InventoryDeserializer() {
        this(null);
    }

    public InventoryDeserializer(Class<Inventory> clazz) {
        super(clazz);
    }

    @Override
    public Inventory deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<InventoryItem> inventoryItems = new LinkedList<>();

        JsonNode rootCounts = p.getCodec().readTree(p);
        rootCounts = rootCounts.get(FIELD_COUNTS);
        for (JsonNode item : rootCounts) {
            InventoryItem inventoryItem = new InventoryItem(item.get(FIELD_CATALOG_OBJECT_ID).asText(),item.get(FIELD_QUANTITY).asText());
            inventoryItems.add(inventoryItem);
        }
        return new Inventory(inventoryItems);
    }
}
