package dev.suvera.opensource.scim2.compliance.tests.group;

import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.javafaker.Faker;
import dev.suvera.opensource.scim2.compliance.biz.ScimResponseValidator;
import dev.suvera.opensource.scim2.compliance.data.*;
import dev.suvera.opensource.scim2.compliance.tests.AbstractTestsCase;
import dev.suvera.opensource.scim2.compliance.utils.FakeData;
import io.scim2.swagger.client.ScimApiException;
import io.scim2.swagger.client.ScimApiResponse;
import io.scim2.swagger.client.api.Scimv2GroupsApi;
import io.scim2.swagger.client.api.Scimv2UsersApi;

import java.util.*;

/**
 * author: suvera
 * date: 9/7/2020 4:03 PM
 */
@SuppressWarnings("FieldCanBeLocal")
public class GroupCrudTests extends AbstractTestsCase {
    private final String CREATE_TEST_NAME = "Create new Group";
    private final String UPDATE_TEST_NAME = "Update Group";
    private final String DELETE_TEST_NAME = "Delete Group";
    private final String LIST_TEST_NAME = "List Group";
    private final String READ_TEST_NAME = "Read a Group";
    private final String FILTER_TEST_NAME = "Search Filter Groups";
    private final String PATCH_TEST_NAME = "Patch Group";


    public GroupCrudTests(
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

        TestCaseResult resultCreate = new TestCaseResult(CREATE_TEST_NAME);
        TestCaseResult resultUpdate = new TestCaseResult(UPDATE_TEST_NAME);
        TestCaseResult resultDelete = new TestCaseResult(DELETE_TEST_NAME);
        TestCaseResult resultList = new TestCaseResult(LIST_TEST_NAME);
        TestCaseResult resultRead = new TestCaseResult(READ_TEST_NAME);
        TestCaseResult resultFilter = new TestCaseResult(FILTER_TEST_NAME);
        TestCaseResult resultPatch = new TestCaseResult(PATCH_TEST_NAME);

        Group group1;
        User user1 = null;
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
            user1 = createUser();
            createResponse = createTest(resultCreate);
            group1 = jsonMapper.readValue(createResponse, Group.class);
        } catch (Exception e) {
            resultCreate.setException(e);
            deleteUser(user1);
            return testCaseResults;
        }

        try {
            readTest(group1, resultRead);
        } catch (Exception e) {
            resultRead.setException(e);
            deleteUser(user1);
            return testCaseResults;
        }

        try {
            listTest(resultList, null);
        } catch (Exception e) {
            resultList.setException(e);
            deleteUser(user1);
            return testCaseResults;
        }

        if (getSpc().getFilter().isSupported()) {
            try {
                listTest(resultFilter, "displayName co \"Stark\"");
            } catch (Exception e) {
                resultFilter.setException(e);
                deleteUser(user1);
                return testCaseResults;
            }
        }

        try {
            updateTest(group1, resultUpdate, createResponse);
        } catch (Exception e) {
            resultUpdate.setException(e);
            deleteUser(user1);
            return testCaseResults;
        }

        if (getSpc().getPatch().isSupported()) {
            try {
                patchTest(group1, user1, resultPatch);
            } catch (Exception e) {
                resultPatch.setException(e);
                deleteUser(user1);
                return testCaseResults;
            }
        }

        try {
            deleteTest(group1, resultDelete);
        } catch (Exception e) {
            resultDelete.setException(e);
            deleteUser(user1);
            return testCaseResults;
        }

        deleteUser(user1);
        return testCaseResults;
    }

    /**
     * Create User- to add members
     */
    private User createUser() throws Exception {
        Map<String, Object> user = FakeData.generateUser(schemas, resourceTypes);
        String body = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

        String endPoint = resourceTypes
                .getResourceBySchema(ScimConstants.SCHEMA_USER)
                .getEndpoint();

        ScimApiResponse<String> response = builder
                .getScimv2UsersClient(endPoint)
                .createUserWithHttpInfo(null, null, body);


        return ScimResponseValidator.processResponse(
                response.getData(),
                JsonLoader.fromString(getSchemas().getJsonSchema(ScimConstants.SCHEMA_USER)),
                User.class
        );
    }

    /**
     * Delete User
     */
    private void deleteUser(User user1) {
        if (user1 == null) {
            return;
        }
        try {
            Scimv2UsersApi api = builder.getScimv2UsersClient(null);
            api.getScimApiClient().setURL(user1.getMeta().getLocation());
            api.deleteUserWithHttpInfo();
        } catch (ScimApiException e) {
            e.printStackTrace();
        }

    }

    /**
     * Patch Group Test
     */
    private void patchTest(Group group1, User user1, TestCaseResult result) throws Exception {
        String body = "{\n" +
                "\t\"schemas\": [\n" +
                "\t\t\"urn:ietf:params:scim:api:messages:2.0:PatchOp\"\n" +
                "\t],\n" +
                "\t\"Operations\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"op\": \"add\",\n" +
                "\t\t\t\"path\": \"members\",\n" +
                "\t\t\t\"value\": [\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"value\": \"" + user1.getId() + "\",\n" +
                "                    \"type\": \"User\",\n" +
                "                    \"$ref\": \"" + user1.getMeta().getLocation() + "\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t]\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";
        result.setRequestBody(body);

        Scimv2GroupsApi api = builder.getScimv2GroupsClient(null);
        api.getScimApiClient().setURL(group1.getMeta().getLocation());
        ScimApiResponse<String> response = api.updateGroupWithHttpInfo(
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
                getSchemas().getJsonSchema(ScimConstants.SCHEMA_GROUP),
                false
        );

        if (report.isSuccess()) {
            result.setSuccess(true);
            return;
        }

        throw new ScimApiException(report.toString() + "\n" + "Actual Response: " + response.getData());
    }

    private void updateTest(Group group1, TestCaseResult result, String body) throws Exception {
        result.setRequestBody(body);

        Scimv2GroupsApi api = builder.getScimv2GroupsClient(null);
        api.getScimApiClient().setURL(group1.getMeta().getLocation());
        ScimApiResponse<String> response = api.updateGroupWithHttpInfo(
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
                getSchemas().getJsonSchema(ScimConstants.SCHEMA_GROUP),
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
                .getResourceBySchema(ScimConstants.SCHEMA_GROUP)
                .getEndpoint();

        Scimv2GroupsApi api = builder.getScimv2GroupsClient(endPoint);

        ScimApiResponse<String> response = api.getGroupWithHttpInfo(
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
                getSchemas().getJsonListSchema(ScimConstants.SCHEMA_GROUP),
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

        Map<String, Object> group = new HashMap<>();
        group.put("schemas", Collections.singletonList(ScimConstants.SCHEMA_GROUP));
        group.put("displayName", (new Faker()).name().username());

        String body = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(group);
        result.setRequestBody(body);

        String endPoint = resourceTypes
                .getResourceBySchema(ScimConstants.SCHEMA_GROUP)
                .getEndpoint();

        ScimApiResponse<String> response = builder
                .getScimv2GroupsClient(endPoint)
                .createGroupWithHttpInfo(null, null, body);

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
                getSchemas().getJsonSchema(ScimConstants.SCHEMA_GROUP),
                false
        );

        if (report.isSuccess()) {
            result.setSuccess(true);
            return response.getData();
        }

        throw new ScimApiException(report.toString() + "\n" + "Actual Response: " + response.getData());
    }

    private void deleteTest(Group group1, TestCaseResult result) throws Exception {
        //System.out.println("Group Meta:" + group1.getMeta());

        Scimv2GroupsApi api = builder.getScimv2GroupsClient(null);
        api.getScimApiClient().setURL(group1.getMeta().getLocation());
        ScimApiResponse<String> response = api.deleteGroupWithHttpInfo();

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


    private void readTest(Group group1, TestCaseResult result) throws Exception {
        Scimv2GroupsApi api = builder.getScimv2GroupsClient(null);
        api.getScimApiClient().setURL(group1.getMeta().getLocation());
        ScimApiResponse<String> response = api.getGroupByIdWithHttpInfo(null, null);

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
                getSchemas().getJsonSchema(ScimConstants.SCHEMA_GROUP),
                false
        );

        if (report.isSuccess()) {
            result.setSuccess(true);
            return;
        }

        throw new ScimApiException(report.toString() + "\n" + "Actual Response: " + response.getData());
    }

}
