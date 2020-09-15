package dev.suvera.opensource.scim2.compliance.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.suvera.opensource.scim2.compliance.data.Schema;
import dev.suvera.opensource.scim2.compliance.data.SchemaExtension;
import dev.suvera.opensource.scim2.compliance.data.json.ScimAttribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * author: suvera
 * date: 9/6/2020 4:10 PM
 */
public class JsonSchemaBuilder {
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private final Schema scimSchema;
    private final List<SchemaExtension> extensions;

    public JsonSchemaBuilder(Schema scimSchema, List<SchemaExtension> extensions) {
        this.scimSchema = scimSchema;
        this.extensions = extensions;
    }

    public String build() {
        Xmap root = Xmap.q();
        root.k("$schema", "http://json-schema.org/draft-04/schema#")
                .k("title", scimSchema.getName())
                .k("description", scimSchema.getDescription())
                .k("type", "object")
                .k("additionalItems", false)
        ;

        buildProperties(root);

        try {
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root.get());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String buildList() {
        Xmap root = Xmap.q();

        Xmap wrapper = Xmap.q();
        wrapper.k("$schema", "http://json-schema.org/draft-04/schema#")
                .k("title", scimSchema.getName())
                .k("description", scimSchema.getDescription())
                .k("type", "object")
                .k("additionalItems", false)
        ;

        Xmap props = Xmap.q();
        props.k("schemas", Xmap.q()
                .k("type", "array")
                .k("items", Xmap.q().k("type", "string").get())
                .get()
        );
        props.k("totalResults", Xmap.q()
                .k("type", "integer")
                .get()
        );
        props.k("startIndex", Xmap.q()
                .k("type", "integer")
                .get()
        );
        props.k("itemsPerPage", Xmap.q()
                .k("type", "integer")
                .get()
        );
        props.k("Resources", Xmap.q()
                .k("type", "array")
                .k("minItems", 0)
                .k("items", root.get())
                .get()
        );

        root.k("type", "object");
        wrapper.k("properties", props.get());

        buildProperties(root);

        try {
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(wrapper.get());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void buildProperties(Xmap propNode) {
        Xmap props = Xmap.q();
        props.k("schemas", Xmap.q()
                .k("type", "array")
                .k("items", Xmap.q().k("type", "string").get())
                .get()
        );
        props.k("id", Xmap.q()
                .k("type", "string")
                .get()
        );
        props.k("externalId", Xmap.q()
                .k("type", "string")
                .get()
        );
        propNode.k("properties", props.get());

        for (ScimAttribute attr : scimSchema.getAttributes()) {
            props.k(attr.getName(), buildAttribute(attr));
        }
        List<String> reqItems = buildRequiredAttrs(scimSchema.getAttributes());

        // Build JsonSchema for scim schema extensions
        if (extensions != null) {
            for (SchemaExtension extension : extensions) {
                if (extension.isRequired()) {
                    reqItems.add(extension.getSchema().getId());
                }

                Xmap extnProps = Xmap.q();
                Xmap extn = Xmap.q()
                        .k("type", "object")
                        .k("properties", extnProps.get());
                props.k(extension.getSchema().getId(), extn.get());
                for (ScimAttribute extnAttr : extension.getSchema().getAttributes()) {
                    extnProps.k(extnAttr.getName(), buildAttribute(extnAttr));
                }
            }
        }
        if (!reqItems.isEmpty()) {
            propNode.k("required", reqItems);
        }
    }

    private Map<String, Object> buildAttribute(ScimAttribute attr) {
        Xmap map = Xmap.q();

        switch (attr.getType()) {
            case "complex":
                if (attr.isMultiValued()) {
                    map.k("type", "array");
                    map.k("minItems", 0);
                    map.k("items", buildArray(attr));
                } else {
                    map.k("type", "object");
                    map.k("properties", buildObject(attr));
                    List<String> reqItems = buildRequiredAttrs(attr.getSubAttributes());
                    if (!reqItems.isEmpty()) {
                        map.k("required", reqItems);
                    }
                }
                break;

            case "boolean":
                map.k("type", "boolean");
                break;

            case "decimal":
                map.k("type", "number");
                break;

            case "integer":
                map.k("type", "integer");
                break;

            default:
                map.k("type", "string");
                break;
        }

        if (attr.getCanonicalValues() != null) {
            map.k("enum", attr.getCanonicalValues());
        }

        return map.get();
    }

    private Map<String, Object> buildObject(ScimAttribute attr) {
        Xmap props = Xmap.q();

        if (attr.getSubAttributes() != null) {
            for (ScimAttribute subAttr : attr.getSubAttributes()) {
                props.k(subAttr.getName(), buildAttribute(subAttr));
            }
        }

        return props.get();
    }

    private Map<String, Object> buildArray(ScimAttribute attr) {
        Xmap map = Xmap.q();
        map.k("type", "object");

        Xmap props = Xmap.q();
        map.k("properties", props.get());

        if (attr.getSubAttributes() != null) {
            for (ScimAttribute subAttr : attr.getSubAttributes()) {
                props.k(subAttr.getName(), buildAttribute(subAttr));
            }
        }

        List<String> reqItems = buildRequiredAttrs(attr.getSubAttributes());
        if (!reqItems.isEmpty()) {
            map.k("required", reqItems);
        }

        return map.get();
    }

    private List<String> buildRequiredAttrs(Collection<ScimAttribute> attributes) {
        List<String> list = new ArrayList<>();

        if (attributes == null) {
            return list;
        }

        for (ScimAttribute attr : attributes) {
            if (attr.isRequired()) {
                list.add(attr.getName());
            }
        }

        return list;
    }
}
