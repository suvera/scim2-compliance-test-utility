package dev.suvera.opensource.scim2.compliance.biz;

import dev.suvera.opensource.scim2.compliance.data.*;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

/**
 * author: suvera
 * date: 9/3/2020 3:13 PM
 */
@Data
public class SchemaFetcher {
    private final static Logger log = LogManager.getLogger(SchemaFetcher.class);
    private final ScimApiClientBuilder clientBuilder;

    public SchemaFetcher(TestContext testContext) {
        this.clientBuilder = new ScimApiClientBuilder(testContext);
    }


    public ServiceProviderConfig fetchServiceProviderConfig() throws Exception {
        Scimv2ServiceProviderConfigApi api = clientBuilder.getServiceProviderConfigClient();
        ScimApiResponse<String> response = api.getServiceProviderConfigWithHttpInfo();

        ScimResponseValidator.processResponseHeaders(
                response.getStatusCode(),
                response.getHeaders(),
                Collections.singletonList(200)
        );

        return ScimResponseValidator.processResponse(
                response.getData(),
                "/schema/ServiceProviderConfig.schema.json",
                ServiceProviderConfig.class
        );
    }

    public ResourceTypes fetchResourceTypes() throws Exception {
        Scimv2ResourceTypeApi api = clientBuilder.getResourceTypeClient();
        ScimApiResponse<String> response = api.getResourceTypesWithHttpInfo();

        ScimResponseValidator.processResponseHeaders(
                response.getStatusCode(),
                response.getHeaders(),
                Collections.singletonList(200)
        );


        ResourceTypes resourceTypes = ScimResponseValidator.processResponse(
                response.getData(),
                "/schema/ResourceTypes.schema.json",
                ResourceTypes.class
        );

        resourceTypes.init();
        if (clientBuilder.getTestContext().isCheckIndResource()) {
            for (ResourceType resourceType : resourceTypes.getResourceTypes()) {
                log.info("Loading Metadata for ResourceType: {}", resourceType.getMeta().getLocation());
                api.getScimApiClient().setURL(resourceType.getMeta().getLocation());
                api.getScimApiClient().setServicePath(null);
                response = api.getRecordByIdWithHttpInfo();


                ScimResponseValidator.processResponseHeaders(
                        response.getStatusCode(),
                        response.getHeaders(),
                        Collections.singletonList(200)
                );
                ScimResponseValidator.processResponse(
                        response.getData(),
                        "/schema/ResourceType.schema.json",
                        Schema.class
                );
            }
        }

        return resourceTypes;
    }

    public Schemas fetchSchemas(ResourceTypes resourceTypes) throws Exception {
        Scimv2SchemaApi api = clientBuilder.getScimv2SchemClient();
        ScimApiResponse<String> response = api.getgetSchemasWithHttpInfo();

        ScimResponseValidator.processResponseHeaders(
                response.getStatusCode(),
                response.getHeaders(),
                Collections.singletonList(200)
        );

        Schemas schemas = ScimResponseValidator.processResponse(
                response.getData(),
                "/schema/Schemas.schema.json",
                Schemas.class
        );

        schemas.setResourceTypes(resourceTypes);

        if (clientBuilder.getTestContext().isCheckIndResource()) {
            for (Schema schema : schemas.getSchemas()) {
                api.getScimApiClient().setURL(schema.getMeta().getLocation());
                api.getScimApiClient().setServicePath(null);
                response = api.getRecordByIdWithHttpInfo();

                ScimResponseValidator.processResponseHeaders(
                        response.getStatusCode(),
                        response.getHeaders(),
                        Collections.singletonList(200)
                );

                ScimResponseValidator.processResponse(
                        response.getData(),
                        "/schema/Schema.schema.json",
                        Schema.class
                );
            }
        }

        return schemas;
    }


}
