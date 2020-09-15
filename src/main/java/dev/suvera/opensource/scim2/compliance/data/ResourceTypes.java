package dev.suvera.opensource.scim2.compliance.data;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: suvera
 * date: 9/3/2020 2:20 PM
 */
@Getter
@ToString
public class ResourceTypes {
    private List<ResourceType> resourceTypes;
    private Map<String, ResourceType> byId = new HashMap<>();
    private Map<String, ResourceType> bySchema = new HashMap<>();

    public ResourceTypes(List<ResourceType> resourceTypes) {
        this.resourceTypes = resourceTypes;
        for (ResourceType type : resourceTypes) {
            if (type.getId() != null) {
                byId.put(type.getId(), type);
            }
            bySchema.put(type.getSchema(), type);
        }
    }

    public ResourceType getResourceBySchema(String schema) {
        return bySchema.get(schema);
    }

    public ResourceType getResourceById(String id) {
        return byId.get(id);
    }
}
