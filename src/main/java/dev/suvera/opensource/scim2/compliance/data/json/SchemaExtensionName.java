package dev.suvera.opensource.scim2.compliance.data.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * author: suvera
 * date: 9/6/2020 1:13 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchemaExtensionName {
    private String schema;
    private boolean required;

    public SchemaExtensionName(Map<?, ?> row) {
        schema = (String) row.get("schema");
        required = (Boolean) row.get("required");
    }
}
