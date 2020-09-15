package dev.suvera.opensource.scim2.compliance.tests.misc;

import com.github.javafaker.Faker;
import dev.suvera.opensource.scim2.compliance.biz.ScimResponseValidator;
import dev.suvera.opensource.scim2.compliance.data.*;
import dev.suvera.opensource.scim2.compliance.tests.AbstractTestsCase;
import dev.suvera.opensource.scim2.compliance.utils.FakeData;
import dev.suvera.opensource.scim2.compliance.utils.Xmap;
import io.scim2.swagger.client.ScimApiException;
import io.scim2.swagger.client.ScimApiResponse;
import io.scim2.swagger.client.api.Scimv2UsersApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * author: suvera
 * date: 9/7/2020 4:03 PM
 */
@SuppressWarnings("FieldCanBeLocal")
public class BulkTests extends AbstractTestsCase {
    private final String DEFAULT_TEST_NAME = "/Bulk Test";

    public BulkTests(
            TestContext testContext,
            ServiceProviderConfig spc,
            ResourceTypes resourceTypes,
            Schemas schemas
    ) {
        super(testContext, spc, resourceTypes, schemas);
    }

    @Override
    public List<TestCaseResult> execute() {
        if (spc.getBulk().isSupported()) {
            return executeSupported();
        } else {
            return executeNotSupported();
        }
    }

    private List<TestCaseResult> executeSupported() {
        List<TestCaseResult> testCaseResults = new ArrayList<>();

        TestCaseResult result = new TestCaseResult(DEFAULT_TEST_NAME);
        result.setRequestMethod("POST");
        testCaseResults.add(result);

        try {
            performBulk(
                    result,
                    builder.getTestContext().getEndPoint() + ScimConstants.URI_BULK
            );
        } catch (Exception e) {
            result.setException(e);
            return testCaseResults;
        }

        return testCaseResults;
    }

    private void performBulk(
            TestCaseResult result,
            String endPoint
    ) throws Exception {
        Scimv2UsersApi api = builder.getScimv2UsersClient(null);
        api.getScimApiClient().setURL(endPoint);

        Xmap bodyMap = buildBulkRequest();

        String body = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bodyMap.get());
        //System.out.println(body);
        result.setRequestBody(body);

        ScimApiResponse<String> response;
        try {
            response = api.getUsersByPostWithHttpInfo(body);

            result.setResponseBody(response.getData());
            result.setResponseCode(response.getStatusCode());
            result.setResponseHeaders(response.getHeaders());
        } catch (ScimApiException e) {
            result.setResponseCode(e.getCode());
            result.setResponseHeaders(e.getResponseHeaders());
            result.setResponseBody(e.getResponseBody());
            return;
        }

        ScimResponseValidator.processResponseHeaders(
                result.getResponseCode(),
                result.getResponseHeaders(),
                Collections.singletonList(200)
        );

        result.setSuccess(true);
    }

    private List<TestCaseResult> executeNotSupported() {
        List<TestCaseResult> testCaseResults = new ArrayList<>();

        TestCaseResult resultNs = new TestCaseResult(DEFAULT_TEST_NAME);
        resultNs.setRequestMethod("POST");
        resultNs.setNotSupported(true);
        resultNs.setResponseCode(501);

        testCaseResults.add(resultNs);

        return testCaseResults;
    }

    private Xmap buildBulkRequest() {
        Xmap root = Xmap.q();

        root.k("schemas", Collections.singletonList("urn:ietf:params:scim:api:messages:2.0:BulkRequest"));
        root.k("Operations", Arrays.asList(buildUser(), buildGroup()));

        return root;
    }

    private Object buildGroup() {
        Xmap p = Xmap.q();

        p.k("method", "POST");
        p.k("path", resourceTypes.getResourceBySchema(ScimConstants.SCHEMA_GROUP).getEndpoint());
        p.k("bulkId", "ytrewq");


        Xmap grp = Xmap.q();
        grp.k("schemas", Collections.singletonList("urn:ietf:params:scim:schemas:core:2.0:Group"));
        grp.k("displayName", (new Faker()).lorem().fixedString(12));

        Xmap members = Xmap.q();
        members.k("type", "User")
                .k("value", "bulkId:qwerty");

        grp.k("members", Collections.singletonList(members.get()));

        p.k("data", grp.get());

        return p.get();
    }

    private Object buildUser() {
        Xmap p = Xmap.q();

        p.k("method", "POST");
        p.k("path", resourceTypes.getResourceBySchema(ScimConstants.SCHEMA_USER).getEndpoint());
        p.k("bulkId", "qwerty");
        p.k("data", FakeData.generateUser(schemas, resourceTypes));

        return p.get();
    }

}
