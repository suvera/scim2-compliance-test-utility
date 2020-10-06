package dev.suvera.opensource.scim2.compliance.utils;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.github.javafaker.PhoneNumber;
import dev.suvera.opensource.scim2.compliance.data.*;
import dev.suvera.opensource.scim2.compliance.data.json.SchemaExtensionName;
import dev.suvera.opensource.scim2.compliance.data.json.ScimAttribute;

import java.util.*;

/**
 * author: suvera
 * date: 9/7/2020 2:57 PM
 */
public class FakeData {

    public static Map<String, Object> generateUser(
            Schemas schemas,
            ResourceTypes resourceTypes
    ) {
        Schema schema = schemas.getSchema(ScimConstants.SCHEMA_USER);
        ResourceType resourceType = resourceTypes.getResourceBySchema(ScimConstants.SCHEMA_USER);

        Faker faker = new Faker();
        Xmap root = Xmap.q();

        Name name = faker.name();
        root.k("userName", name.username());
        root.k("externalId", name.username());

        root.k("displayName", name.name());
        root.k("nickName", faker.funnyName().name());

        root.k("active", true);
        root.k("title", name.title());

        PhoneNumber phone = faker.phoneNumber();
        root.k(
                "phoneNumbers",
                Collections.singletonList(
                        Xmap.q()
                                .k("value", phone.cellPhone())
                                .k("primary", true)
                                .get()
                )
        );

        Address address = faker.address();
        root.k(
                "addresses",
                Collections.singletonList(
                        Xmap.q()
                                .k("formatted", address.fullAddress())
                                .k("streetAddress", address.streetAddress())
                                .k("locality", address.cityName())
                                //.k("region", address.state())
                                .k("postalCode", address.zipCode())
                                .k("country", "Philippines")
                                .k("primary", true)
                                .get()
                )
        );

        root.k(
                "emails",
                Collections.singletonList(
                        Xmap.q()
                                .k("value", name.username() + "@opensource.suvera.dev")
                                .k("primary", true)
                                .get()
                )
        );

        root.k(
                "name",
                Xmap.q()
                        .k("familyName", name.lastName())
                        .k("givenName", name.firstName())
                        .k("formatted", name.fullName())
                        .k("honorificPrefix", name.prefix())
                        .get()
        );

        root.k("password", name.username());

        List<String> schemaList = new ArrayList<>();
        schemaList.add(resourceType.getSchema());
        if (resourceType.getSchemaExtensions() != null) {
            SchemaExtensionName ext = resourceType.getSchemaExtensions();
            schemaList.add(ext.getSchema());
            root.k(ext.getSchema(), getExtensionData(ext.getSchema(), schemas, resourceTypes));
        }
        root.k("schemas", schemaList);

        return root.get();
    }

    private static Map<String, Object> getExtensionData(
            String schemaName,
            Schemas schemas,
            ResourceTypes resourceTypes
    ) {
        Schema schema = schemas.getSchema(schemaName);
        Xmap root = getAttributesData(schema.getAttributes());
        return root.get();
    }

    private static Xmap getAttributesData(Collection<ScimAttribute> attributes) {
        Faker faker = new Faker();
        Xmap root = Xmap.q();

        for (ScimAttribute attr : attributes) {
            boolean hasEnum = (attr.getCanonicalValues() != null && !attr.getCanonicalValues().isEmpty());

            switch (attr.getType()) {
                case "complex":
                    if (attr.isMultiValued()) {
                        root.k(attr.getName(),
                                Collections.singletonList(getAttributesData(attr.getSubAttributes()).get()));
                    } else {
                        root.k(attr.getName(), getAttributesData(attr.getSubAttributes()).get());
                    }
                    break;

                case "boolean":
                    root.k(attr.getName(), true);
                    break;

                case "decimal":
                    if (hasEnum) {
                        root.k(attr.getName(), Double.valueOf(attr.getCanonicalValues().get(0)));
                    } else {
                        root.k(attr.getName(), faker.number().randomDouble(2, 0, 100));
                    }
                    break;

                case "integer":
                    if (hasEnum) {
                        root.k(attr.getName(), Long.valueOf(0L + attr.getCanonicalValues().get(0)));
                    } else {
                        root.k(attr.getName(), faker.number().numberBetween(0, 99999));
                    }
                    break;

                default:
                    if (hasEnum) {
                        root.k(attr.getName(), attr.getCanonicalValues().get(0));
                    } else {
                        root.k(attr.getName(), faker.lorem().fixedString(10));
                    }
                    break;
            }
        }

        return root;
    }
}
