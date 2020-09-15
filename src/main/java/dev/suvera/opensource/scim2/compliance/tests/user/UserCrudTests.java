package dev.suvera.opensource.scim2.compliance.tests.user;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import dev.suvera.opensource.scim2.compliance.biz.ScimResponseValidator;
import dev.suvera.opensource.scim2.compliance.data.*;
import dev.suvera.opensource.scim2.compliance.tests.AbstractTestsCase;
import dev.suvera.opensource.scim2.compliance.utils.FakeData;
import io.scim2.swagger.client.ScimApiException;
import io.scim2.swagger.client.ScimApiResponse;
import io.scim2.swagger.client.api.Scimv2UsersApi;

import java.util.*;

/**
 * author: suvera
 * date: 9/7/2020 4:03 PM
 */
@SuppressWarnings("FieldCanBeLocal")
public class UserCrudTests extends AbstractTestsCase {
    private final String CREATE_TEST_NAME = "Create new User";
    private final String UPDATE_TEST_NAME = "Update User";
    private final String DELETE_TEST_NAME = "Delete User";
    private final String LIST_TEST_NAME = "List User";
    private final String READ_TEST_NAME = "Read a User";
    private final String FILTER_TEST_NAME = "Search Filter Users";
    private final String PATCH_TEST_NAME = "Patch User";


    public UserCrudTests(
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

        System.out.println("User Filter: " + getSpc().getFilter());
        System.out.println("User Filter-2: " + spc.getFilter());

        TestCaseResult resultCreate = new TestCaseResult(CREATE_TEST_NAME);
        TestCaseResult resultUpdate = new TestCaseResult(UPDATE_TEST_NAME);
        TestCaseResult resultDelete = new TestCaseResult(DELETE_TEST_NAME);
        TestCaseResult resultList = new TestCaseResult(LIST_TEST_NAME);
        TestCaseResult resultRead = new TestCaseResult(READ_TEST_NAME);
        TestCaseResult resultFilter = new TestCaseResult(FILTER_TEST_NAME);
        TestCaseResult resultPatch = new TestCaseResult(PATCH_TEST_NAME);

        User user1;
        String createResponse;

        resultCreate.setRequestMethod("POST");
        resultUpdate.setRequestMethod("PUT");
        resultDelete.setRequestMethod("DELETE");
        resultList.setRequestMethod("GET");
        resultFilter.setRequestMethod("GET");
        resultRead.setRequestMethod("GET");
        resultPatch.setRequestMethod("PATCH");

        testCaseResults.add(resultCreate);
        testCaseResults.add(resultRead);
        testCaseResults.add(resultList);
        if (getSpc().getFilter().isSupported()) {
            testCaseResults.add(resultFilter);
        }
        testCaseResults.add(resultUpdate);
        if (getSpc().getPatch().isSupported()) {
            testCaseResults.add(resultPatch);
        }
        testCaseResults.add(resultDelete);


        try {
            createResponse = createTest(resultCreate);
            user1 = jsonMapper.readValue(createResponse, User.class);
        } catch (Exception e) {
            resultCreate.setException(e);
            return testCaseResults;
        }

        try {
            readTest(user1, resultRead);
        } catch (Exception e) {
            resultRead.setException(e);
            return testCaseResults;
        }

        try {
            listTest(resultList, null);
        } catch (Exception e) {
            resultList.setException(e);
            return testCaseResults;
        }

        if (getSpc().getFilter().isSupported()) {
            try {
                listTest(resultFilter, "name.familyName co \"Stark\"");
            } catch (Exception e) {
                resultFilter.setException(e);
                return testCaseResults;
            }
        }

        try {
            updateTest(user1, resultUpdate, createResponse);
        } catch (Exception e) {
            resultUpdate.setException(e);
            return testCaseResults;
        }

        if (getSpc().getPatch().isSupported()) {
            try {
                patchTest(user1, resultPatch);
            } catch (Exception e) {
                resultPatch.setException(e);
                return testCaseResults;
            }
        }

        try {
            deleteTest(user1, resultDelete);
        } catch (Exception e) {
            resultDelete.setException(e);
            return testCaseResults;
        }

        return testCaseResults;
    }

    /**
     * Patch User Test
     */
    private void patchTest(User user1, TestCaseResult result) throws Exception {
        String body = "{\n" +
                "\t\"schemas\": [\n" +
                "\t\t\"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
                "\t],\n" +
                "\t\"Operations\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"op\": \"replace\",\n" +
                "\t\t\t\"path\": \"name.familyName\",\n" +
                "\t\t\t\"value\": \"Stagger\"\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";
        result.setRequestBody(body);

        Scimv2UsersApi api = builder.getScimv2UsersClient(null);
        api.getScimApiClient().setURL(user1.getMeta().getLocation());
        ScimApiResponse<String> response = api.updateUserWithHttpInfo(
                null,
                null,
                body,
                "PATCH"
        );

        result.setResponseBody(response.getData());
        result.setResponseCode(response.getStatusCode());
        result.setResponseHeaders(response.getHeaders());

        ScimResponseValidator.processResponseHeaders(
                response.getStatusCode(),
                response.getHeaders(),
                Arrays.asList(200, 204)
        );

        if (response.getStatusCode() == 204) {
            result.setSuccess(true);
            return;
        }

        ProcessingReport report = ScimResponseValidator.validateResponse(
                response.getData(),
                getSchemas().getJsonSchema(ScimConstants.SCHEMA_USER),
                false
        );

        if (report.isSuccess()) {
            result.setSuccess(true);
            return;
        }


        throw new ScimApiException(report.toString() + "\n" + "Actual Response: " + response.getData());
    }

    private void updateTest(User user1, TestCaseResult result, String body) throws Exception {
        result.setRequestBody(body);

        Scimv2UsersApi api = builder.getScimv2UsersClient(null);
        api.getScimApiClient().setURL(user1.getMeta().getLocation());
        ScimApiResponse<String> response = api.updateUserWithHttpInfo(
                null,
                null,
                body,
                "PUT"
        );

        result.setResponseBody(response.getData());
        result.setResponseCode(response.getStatusCode());
        result.setResponseHeaders(response.getHeaders());

        ScimResponseValidator.processResponseHeaders(
                response.getStatusCode(),
                response.getHeaders(),
                Collections.singletonList(200)
        );

        ProcessingReport report = ScimResponseValidator.validateResponse(
                response.getData(),
                getSchemas().getJsonSchema(ScimConstants.SCHEMA_USER),
                false
        );

        if (report.isSuccess()) {
            result.setSuccess(true);
            return;
        }

        throw new ScimApiException(report.toString() + "\n" + "Actual Response: " + response.getData());
    }

    private void listTest(TestCaseResult result, String filter) throws Exception {
        String endPoint = resourceTypes
                .getResourceBySchema(ScimConstants.SCHEMA_USER)
                .getEndpoint();

        Scimv2UsersApi api = builder.getScimv2UsersClient(endPoint);

        ScimApiResponse<String> response = api.getUserWithHttpInfo(
                null,
                null,
                filter,
                0,
                10,
                null,
                null
        );

        result.setResponseBody(response.getData());
        result.setResponseCode(response.getStatusCode());
        result.setResponseHeaders(response.getHeaders());

        ScimResponseValidator.processResponseHeaders(
                response.getStatusCode(),
                response.getHeaders(),
                Collections.singletonList(200)
        );

        ProcessingReport report = ScimResponseValidator.validateResponse(
                response.getData(),
                getSchemas().getJsonListSchema(ScimConstants.SCHEMA_USER),
                false
        );

        if (report.isSuccess()) {
            result.setSuccess(true);
            return;
        }

        throw new ScimApiException(report.toString() + "\n" + "Actual Response: " + response.getData());
    }

    private String createTest(TestCaseResult result) throws Exception {
        result.setRequestMethod("POST");

        Map<String, Object> user = FakeData.generateUser(schemas, resourceTypes);
        String body = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
        result.setRequestBody(body);

        String endPoint = resourceTypes
                .getResourceBySchema(ScimConstants.SCHEMA_USER)
                .getEndpoint();

        ScimApiResponse<String> response = builder
                .getScimv2UsersClient(endPoint)
                .createUserWithHttpInfo(null, null, body);

        result.setResponseBody(response.getData());
        result.setResponseCode(response.getStatusCode());
        result.setResponseHeaders(response.getHeaders());

        ScimResponseValidator.processResponseHeaders(
                response.getStatusCode(),
                response.getHeaders(),
                Collections.singletonList(201)
        );

        ProcessingReport report = ScimResponseValidator.validateResponse(
                response.getData(),
                getSchemas().getJsonSchema(ScimConstants.SCHEMA_USER),
                false
        );

        if (report.isSuccess()) {
            result.setSuccess(true);
            return response.getData();
        }

        throw new ScimApiException(report.toString() + "\n" + "Actual Response: " + response.getData());
    }

    private void deleteTest(User user1, TestCaseResult result) throws Exception {
        Scimv2UsersApi api = builder.getScimv2UsersClient(null);
        api.getScimApiClient().setURL(user1.getMeta().getLocation());
        ScimApiResponse<String> response = api.deleteUserWithHttpInfo();

        result.setResponseBody(response.getData());
        result.setResponseCode(response.getStatusCode());
        result.setResponseHeaders(response.getHeaders());

        ScimResponseValidator.processResponseHeaders(
                response.getStatusCode(),
                response.getHeaders(),
                Collections.singletonList(204)
        );

        result.setSuccess(true);
    }


    private void readTest(User user1, TestCaseResult result) throws Exception {
        Scimv2UsersApi api = builder.getScimv2UsersClient(null);
        api.getScimApiClient().setURL(user1.getMeta().getLocation());
        ScimApiResponse<String> response = api.getUserByIdWithHttpInfo(null, null);

        result.setResponseBody(response.getData());
        result.setResponseCode(response.getStatusCode());
        result.setResponseHeaders(response.getHeaders());

        ScimResponseValidator.processResponseHeaders(
                response.getStatusCode(),
                response.getHeaders(),
                Collections.singletonList(200)
        );

        ProcessingReport report = ScimResponseValidator.validateResponse(
                response.getData(),
                getSchemas().getJsonSchema(ScimConstants.SCHEMA_USER),
                false
        );

        if (report.isSuccess()) {
            result.setSuccess(true);
            return;
        }

        throw new ScimApiException(report.toString() + "\n" + "Actual Response: " + response.getData());
    }

}
