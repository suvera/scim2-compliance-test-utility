package dev.suvera.opensource.scim2.compliance.data.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * author: suvera
 * date: 9/6/2020 3:06 PM
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class ScimAttribute {
    private String name;
    private String type;
    private List<ScimAttribute> subAttributes;
    private boolean multiValued;
    private String description;
    private boolean required;
    private List<String> canonicalValues;
    private boolean caseExact;

    private String mutability;
    private String returned;
    private String uniqueness;
    private List<String> referenceTypes;

    public boolean hasSubAttribute(String name) {
        if (subAttributes == null) {
            return false;
        }
        for (ScimAttribute subAttribute : subAttributes) {
            if (subAttribute.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public ScimAttribute(String name, String type) {
        this.name = name;
        this.type = type;
        subAttributes = null;
        canonicalValues = null;
        referenceTypes = null;
    }
}
