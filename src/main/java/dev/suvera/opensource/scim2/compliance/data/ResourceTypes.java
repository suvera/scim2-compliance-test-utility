package dev.suvera.opensource.scim2.compliance.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: suvera
 * date: 9/3/2020 2:20 PM
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class ResourceTypes {
    @JsonProperty("Resources")
    private List<ResourceType> resourceTypes;

    @JsonIgnore
    private Map<String, ResourceType> byId = new HashMap<>();
    @JsonIgnore
    private Map<String, ResourceType> bySchema = new HashMap<>();

    public void init() {
        for (ResourceType type : resourceTypes) {
            if (type.getId() != null) {
                byId.put(type.getId(), type);
            }
            bySchema.put(type.getSchema(), type);
        }
    }

    @JsonIgnore
    public ResourceType getResourceBySchema(String schema) {
        return bySchema.get(schema);
    }

    @JsonIgnore
    public ResourceType getResourceById(String id) {
        return byId.get(id);
    }
}
