package dev.suvera.opensource.scim2.compliance.data;

import java.util.HashSet;
import java.util.Set;

/**
 * author: suvera
 * date: 9/6/2020 2:19 PM
 */
public class ScimConstants {
    public static final String SCHEMA_USER = "urn:ietf:params:scim:schemas:core:2.0:User";
    public static final String SCHEMA_ENTERPRISE_USER =
            "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User";
    public static final String SCHEMA_GROUP = "urn:ietf:params:scim:schemas:core:2.0:Group";
    public static final String SCHEMA_SERVICE_PROVIDER_CONFIG =
            "urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig";
    public static final String SCHEMA_RESOURCE_TYPE = "urn:ietf:params:scim:schemas:core:2.0:ResourceType";
    public static final String SCHEMA_SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:Schema";

    public static final String MESSAGE_BULK_REQUEST = "urn:ietf:params:scim:api:messages:2.0:BulkRequest";
    public static final String MESSAGE_BULK_RESPONSE = "urn:ietf:params:scim:api:messages:2.0:BulkResponse";
    public static final String MESSAGE_ERROR = "urn:ietf:params:scim:api:messages:2.0:Error";
    public static final String MESSAGE_PATCH_OP = "urn:ietf:params:scim:api:messages:2.0:PatchOp";
    public static final String MESSAGE_LIST_RESPONSE = "urn:ietf:params:scim:api:messages:2.0:ListResponse";
    public static final String MESSAGE_SEARCH_REQUEST = "urn:ietf:params:scim:api:messages:2.0:SearchRequest";

    public static final String[] CONTENT_TYPES = new String[]{"application/scim+json", "application/json"};

    public static final String URI_ME = "/Me";
    public static final String URI_SEARCH = "/.search";
    public static final String URI_BULK = "/Bulk";

    public static final Set<String> coreSchemas = new HashSet<>();

    static {
        coreSchemas.add(SCHEMA_USER);
        coreSchemas.add(SCHEMA_GROUP);
        coreSchemas.add(SCHEMA_SERVICE_PROVIDER_CONFIG);
        coreSchemas.add(SCHEMA_RESOURCE_TYPE);
        coreSchemas.add(SCHEMA_SCHEMA);
    }

}
