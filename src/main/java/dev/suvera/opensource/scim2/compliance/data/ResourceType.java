package dev.suvera.opensource.scim2.compliance.data;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.suvera.opensource.scim2.compliance.data.json.SchemaExtensionName;
import dev.suvera.opensource.scim2.compliance.data.json.ScimMeta;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * author: suvera
 * date: 9/3/2020 2:19 PM
 */
@SuppressWarnings("unused")
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResourceType {
    private List<String> schemas;
    private String id;
    private ScimMeta meta;

    private String name;
    private String description;

    private String endpoint;
    private String schema;

    @JsonIgnore
    private List<SchemaExtensionName> schemaExtensions = new ArrayList<>();

    @JsonAnySetter
    public void addOtherInfo(String key, Object value) {
        if ("schemaExtensions".equals(key)) {
            if (value instanceof Collection) {
                for (Object item : (Collection<?>) value) {
                    schemaExtensions.add(new SchemaExtensionName((Map<?, ?>) item));
                }
            } else if (value instanceof Map) {
                schemaExtensions.add(new SchemaExtensionName((Map<?, ?>) value));
            } else if (value != null) {
                throw new RuntimeException("Could not understand schemaExtensions attribute value "
                        + value.getClass() + ", " + value);
            }


        }
    }
}
