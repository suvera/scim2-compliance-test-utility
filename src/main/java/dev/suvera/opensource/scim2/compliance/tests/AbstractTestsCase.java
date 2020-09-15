package dev.suvera.opensource.scim2.compliance.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.suvera.opensource.scim2.compliance.biz.ScimApiClientBuilder;
import dev.suvera.opensource.scim2.compliance.data.ResourceTypes;
import dev.suvera.opensource.scim2.compliance.data.Schemas;
import dev.suvera.opensource.scim2.compliance.data.ServiceProviderConfig;
import dev.suvera.opensource.scim2.compliance.data.TestContext;
import lombok.Data;

/**
 * author: suvera
 * date: 9/3/2020 2:23 PM
 */
@Data
public abstract class AbstractTestsCase implements TestsCase {
    protected static final ObjectMapper jsonMapper = new ObjectMapper();

    protected ScimApiClientBuilder builder;
    protected ServiceProviderConfig spc;
    protected ResourceTypes resourceTypes;
    protected Schemas schemas;

    public AbstractTestsCase(
            TestContext testContext,
            ServiceProviderConfig spc,
            ResourceTypes resourceTypes,
            Schemas schemas
    ) {
        builder = new ScimApiClientBuilder(testContext);
        this.spc = spc;
        this.resourceTypes = resourceTypes;
        this.schemas = schemas;
    }

}
