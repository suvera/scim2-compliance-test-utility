package dev.suvera.opensource.scim2.compliance.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import dev.suvera.opensource.scim2.compliance.data.json.ScimAttribute;
import dev.suvera.opensource.scim2.compliance.data.json.ScimMeta;
import lombok.Data;

import java.util.Collection;
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
public class Schema {
    private List<String> schemas;
    private String id;
    private ScimMeta meta;

    private String name;
    private String description;

    @JsonIgnore
    private Map<String, ScimAttribute> attributes = new HashMap<>();

    @JsonSetter("attributes")
    public void setAttributes(List<ScimAttribute> attrs) {
        for (ScimAttribute attr : attrs) {
            attributes.put(attr.getName(), attr);
        }
    }

    public Collection<ScimAttribute> getAttributes() {
        return attributes.values();
    }

    public ScimAttribute getAttribute(String name) {
        return attributes.get(name);
    }

    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    public boolean hasSubAttribute(String name, String subName) {
        return attributes.get(name) != null && attributes.get(name).hasSubAttribute(subName);
    }
}
