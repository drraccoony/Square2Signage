package org.tyto.square.client.model.square;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class CatalogDeserializer extends StdDeserializer<Catalog> {

    private static final String FIELD_NAME_OBJECTS = "objects";
    private static final String FIELD_NAME_TYPE = "type";
    private static final String FIELD_NAME_NAME = "name";
    private static final String FIELD_NAME_ID = "id";
    private static final String FIELD_NAME_ITEM_DATA = "item_data";
    private static final String FIELD_NAME_VARIATIONS = "variations";
    private static final String FIELD_NAME_ITEM_VARIATION_DATA = "item_variation_data";

    private static final String TYPE_ITEM = "ITEM";

    public CatalogDeserializer() {
        this(null);
    }

    public CatalogDeserializer(Class<Catalog> clazz) {
        super(clazz);
    }

    @Override
    public Catalog deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<CatalogItem> items = new LinkedList<>();
        try {
            JsonNode rootNode = p.getCodec().readTree(p);
            if (!rootNode.isEmpty()
                    && (rootNode = rootNode.get(FIELD_NAME_OBJECTS)) != null
                    && rootNode instanceof ArrayNode) {
                for (JsonNode catalogObject : rootNode) {
                    JsonNode workingNode = catalogObject.get(FIELD_NAME_TYPE);
                    if (workingNode != null
                            && workingNode.asText().equals(TYPE_ITEM)) {
                        workingNode = catalogObject.get(FIELD_NAME_ITEM_DATA);
                        String itemName = workingNode.get(FIELD_NAME_NAME).asText();
                        for (JsonNode variation : workingNode.get(FIELD_NAME_VARIATIONS)) {
                            String variationId = variation.get(FIELD_NAME_ID).asText();
                            String variationName = itemName + " - " + variation.get(FIELD_NAME_ITEM_VARIATION_DATA).get(FIELD_NAME_NAME).asText();
                            items.add(new CatalogItem(variationId, variationName));
                        }
                    }
                }
            }
        } catch (JsonParseException e) {
            log.error("Failed to deserialize Catalog due to error: " + e.getMessage(), e);
        }
        return new Catalog(items);
    }
}
