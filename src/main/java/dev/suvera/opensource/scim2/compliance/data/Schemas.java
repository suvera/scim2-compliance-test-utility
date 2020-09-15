package dev.suvera.opensource.scim2.compliance.data;

import dev.suvera.opensource.scim2.compliance.data.json.SchemaExtensionName;
import dev.suvera.opensource.scim2.compliance.utils.JsonSchemaBuilder;
import io.scim2.swagger.client.ScimApiException;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: suvera
 * date: 9/3/2020 2:19 PM
 */
@Data
@ToString
public class Schemas {

    private List<Schema> schemas;
    private ResourceTypes resourceTypes;
    private Map<String, Schema> byId = new HashMap<>();
    private Map<String, String> jsonSchema = new HashMap<>();
    private Map<String, String> jsonListSchema = new HashMap<>();

    public Schemas(List<Schema> schemas, ResourceTypes resourceTypes) throws ScimApiException {
        this.schemas = schemas;
        this.resourceTypes = resourceTypes;
        for (Schema schema : schemas) {
            byId.put(schema.getId(), schema);
            if (ScimConstants.coreSchemas.contains(schema.getId())
                    && resourceTypes.getResourceBySchema(schema.getId()) == null) {
                throw new ScimApiException("Could not find ResourceType for core Schema '"
                        + schema.getId() + "'");
            }
        }

        for (Schema schema : schemas) {
            buildJsonSchema(schema);
        }
    }

    private void buildJsonSchema(Schema schema) throws ScimApiException {

        ResourceType resourceType = resourceTypes.getResourceBySchema(schema.getId());
        List<SchemaExtension> extensions = new ArrayList<>();

        if (resourceType != null && resourceType.getSchemaExtensions() != null) {
            for (SchemaExtensionName extensionName : resourceType.getSchemaExtensions()) {
                Schema extension = byId.get(extensionName.getSchema());
                if (extension == null) {
                    throw new ScimApiException("Could not find Schema for Extension '"
                            + extensionName.getSchema() + "' for Core Schema " + schema.getId());
                }

                if (schema.getId().equals(extensionName.getSchema())) {
                    throw new ScimApiException("SCIM Schema extension cannot be itself, " +
                            "Circular References are not allowed " + extensionName.getSchema());
                }

                if (ScimConstants.coreSchemas.contains(extensionName.getSchema())) {
                    throw new ScimApiException("SCIM Schema extension cannot be one of Core Schema's " +
                            extensionName.getSchema());
                }

                extensions.add(new SchemaExtension(extension, extensionName.isRequired()));
            }
        }

        JsonSchemaBuilder builder = new JsonSchemaBuilder(
                schema,
                extensions
        );
        jsonSchema.put(schema.getId(), builder.build());
        jsonListSchema.put(schema.getId(), builder.buildList());
    }

    public Schema getSchema(String id) {
        return byId.get(id);
    }

    public String getJsonSchema(String id) {
        return jsonSchema.get(id);
    }

    public String getJsonListSchema(String id) {
        return jsonListSchema.get(id);
    }

}
