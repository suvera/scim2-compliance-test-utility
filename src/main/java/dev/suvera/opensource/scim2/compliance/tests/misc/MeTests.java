package dev.suvera.opensource.scim2.compliance.tests.misc;

import dev.suvera.opensource.scim2.compliance.biz.ScimResponseValidator;
import dev.suvera.opensource.scim2.compliance.data.*;
import dev.suvera.opensource.scim2.compliance.tests.AbstractTestsCase;
import io.scim2.swagger.client.ScimApiException;
import io.scim2.swagger.client.ScimApiResponse;
import io.scim2.swagger.client.api.Scimv2UsersApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author: suvera
 * date: 9/7/2020 4:03 PM
 */
@SuppressWarnings("FieldCanBeLocal")
public class MeTests extends AbstractTestsCase {
    private final String READ_TEST_NAME = "/Me Get Test";


    public MeTests(
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

        TestCaseResult resultRead = new TestCaseResult(READ_TEST_NAME);
        resultRead.setRequestMethod("GET");

        testCaseResults.add(resultRead);
        try {
            readTest(resultRead);
        } catch (Exception e) {
            resultRead.setException(e);
            return testCaseResults;
        }
        return testCaseResults;
    }

    private void readTest(TestCaseResult result) throws Exception {
        Scimv2UsersApi api = builder.getScimv2UsersClient(null);
        api.getScimApiClient().setURL(builder.getTestContext().getEndPoint() + ScimConstants.URI_ME);
        ScimApiResponse<String> response;
        try {
            response = api.getUserWithHttpInfo(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            result.setResponseCode(response.getStatusCode());
            result.setResponseHeaders(response.getHeaders());
            result.setResponseBody(response.getData());
        } catch (ScimApiException e) {
            if (e.getCode() == 501 || e.getCode() == 308) {
                result.setNotSupported(true);
            }
            result.setResponseCode(e.getCode());
            result.setResponseHeaders(e.getResponseHeaders());
            result.setResponseBody(e.getResponseBody());
        }

        ScimResponseValidator.processResponseHeaders(
                result.getResponseCode(),
                result.getResponseHeaders(),
                Arrays.asList(501, 308, 200, 201)
        );

        result.setSuccess(true);
    }

}
