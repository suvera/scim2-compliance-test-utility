package dev.suvera.opensource.scim2.compliance.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.suvera.opensource.scim2.compliance.data.json.SchemaExtensionName;
import dev.suvera.opensource.scim2.compliance.utils.JsonSchemaBuilder;
import io.scim2.swagger.client.ScimApiException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: suvera
 * date: 9/3/2020 2:19 PM
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class Schemas {

    @JsonProperty("Resources")
    private List<Schema> schemas;

    @JsonIgnore
    private ResourceTypes resourceTypes;

    @JsonIgnore
    private Map<String, Schema> byId = new HashMap<>();

    @JsonIgnore
    private Map<String, String> jsonSchema = new HashMap<>();

    @JsonIgnore
    private Map<String, String> jsonListSchema = new HashMap<>();

    public void setResourceTypes(ResourceTypes resourceTypes) throws ScimApiException {
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

    @JsonIgnore
    private void buildJsonSchema(Schema schema) throws ScimApiException {

        ResourceType resourceType = resourceTypes.getResourceBySchema(schema.getId());
        List<SchemaExtension> extensions = new ArrayList<>();

        if (resourceType != null && resourceType.getSchemaExtensions() != null) {
            SchemaExtensionName extensionName = resourceType.getSchemaExtensions();
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

        JsonSchemaBuilder builder = new JsonSchemaBuilder(
                schema,
                extensions
        );
        jsonSchema.put(schema.getId(), builder.build());
        jsonListSchema.put(schema.getId(), builder.buildList());
    }

    @JsonIgnore
    public Schema getSchema(String id) {
        return byId.get(id);
    }

    @JsonIgnore
    public String getJsonSchema(String id) {
        return jsonSchema.get(id);
    }

    @JsonIgnore
    public String getJsonListSchema(String id) {
        return jsonListSchema.get(id);
    }

}
