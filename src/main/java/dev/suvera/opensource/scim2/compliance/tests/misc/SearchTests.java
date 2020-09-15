package dev.suvera.opensource.scim2.compliance.tests.misc;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import dev.suvera.opensource.scim2.compliance.biz.ScimResponseValidator;
import dev.suvera.opensource.scim2.compliance.data.*;
import dev.suvera.opensource.scim2.compliance.tests.AbstractTestsCase;
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
public class SearchTests extends AbstractTestsCase {
    private final String SEARCH_ROOT_NAME = "/.search on Root Test";
    private final String SEARCH_USERS_NAME = "/.search on /Users Test";
    private final String SEARCH_GROUPS_NAME = "/.search on /Groups Test";


    public SearchTests(
            TestContext testContext,
            ServiceProviderConfig spc,
            ResourceTypes resourceTypes,
            Schemas schemas
    ) {
        super(testContext, spc, resourceTypes, schemas);
    }

    @Override
    public List<TestCaseResult> execute() {
        List<TestCaseResult> testCaseResults = new ArrayList<>();
        TestCaseResult result;

        result = new TestCaseResult(SEARCH_ROOT_NAME);
        result.setRequestMethod("POST");
        testCaseResults.add(result);
        try {
            performSearch(
                    result,
                    builder.getTestContext().getEndPoint() + ScimConstants.URI_SEARCH,
                    "(meta.resourceType eq \"User\") and (displayName sw \"s\")",
                    null
            );
        } catch (Exception e) {
            result.setException(e);
            return testCaseResults;
        }

        result = new TestCaseResult(SEARCH_USERS_NAME);
        result.setRequestMethod("POST");
        testCaseResults.add(result);
        try {
            performSearch(
                    result,
                    builder.getTestContext().getEndPoint()
                            + resourceTypes.getResourceBySchema(ScimConstants.SCHEMA_USER).getEndpoint()
                            + ScimConstants.URI_SEARCH,
                    "displayName sw \"s\"",
                    ScimConstants.SCHEMA_USER
            );
        } catch (Exception e) {
            result.setException(e);
            return testCaseResults;
        }

        result = new TestCaseResult(SEARCH_GROUPS_NAME);
        result.setRequestMethod("POST");
        testCaseResults.add(result);
        try {
            performSearch(
                    result,
                    builder.getTestContext().getEndPoint()
                            + resourceTypes.getResourceBySchema(ScimConstants.SCHEMA_GROUP).getEndpoint()
                            + ScimConstants.URI_SEARCH,
                    "displayName sw \"s\"",
                    ScimConstants.SCHEMA_GROUP
            );
        } catch (Exception e) {
            result.setException(e);
            return testCaseResults;
        }

        return testCaseResults;
    }

    private void performSearch(
            TestCaseResult result,
            String endPoint,
            String filter,
            String schemaId
    ) throws Exception {
        Scimv2UsersApi api = builder.getScimv2UsersClient(null);
        api.getScimApiClient().setURL(endPoint);

        Xmap node = Xmap.q()
                .k("schemas", Collections.singletonList(ScimConstants.MESSAGE_SEARCH_REQUEST))
                .k("filter", filter)
                .k("startIndex", 1)
                .k("count", 0);

        String body = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node.get());
        result.setRequestBody(body);

        ScimApiResponse<String> response;
        try {
            response = api.getUsersByPostWithHttpInfo(body);

            result.setResponseBody(response.getData());
            result.setResponseCode(response.getStatusCode());
            result.setResponseHeaders(response.getHeaders());
        } catch (ScimApiException e) {
            if (e.getCode() == 501) {
                result.setNotSupported(true);
            }
            result.setResponseCode(e.getCode());
            result.setResponseHeaders(e.getResponseHeaders());
            result.setResponseBody(e.getResponseBody());
            return;
        }

        ScimResponseValidator.processResponseHeaders(
                result.getResponseCode(),
                result.getResponseHeaders(),
                Arrays.asList(501, 200)
        );

        if (schemaId != null && !schemaId.isEmpty()) {
            ProcessingReport report = ScimResponseValidator.validateResponse(
                    result.getResponseBody(),
                    getSchemas().getJsonListSchema(schemaId),
                    false
            );

            result.setSuccess(report.isSuccess());
            return;
        }

        result.setSuccess(true);
    }

}
