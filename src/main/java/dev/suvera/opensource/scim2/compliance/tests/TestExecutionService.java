package dev.suvera.opensource.scim2.compliance.tests;

import dev.suvera.opensource.scim2.compliance.biz.SchemaFetcher;
import dev.suvera.opensource.scim2.compliance.data.*;
import dev.suvera.opensource.scim2.compliance.tests.group.GroupCrudTests;
import dev.suvera.opensource.scim2.compliance.tests.misc.BulkTests;
import dev.suvera.opensource.scim2.compliance.tests.misc.MeTests;
import dev.suvera.opensource.scim2.compliance.tests.misc.SearchTests;
import dev.suvera.opensource.scim2.compliance.tests.user.UserCrudTests;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * author: suvera
 * date: 9/7/2020 5:04 PM
 */
@Service
@Data
public class TestExecutionService {

    @Autowired
    private TestDatabaseService database;

    @Async
    public void executeTests(
            String runId,
            TestContext testContext
    ) {

        System.out.println(testContext);

        SchemaFetcher fetcher = new SchemaFetcher(testContext);
        ServiceProviderConfig spc;
        ResourceTypes resourceTypes;
        Schemas schemas;
        TestCaseResult result;

        System.out.println("Fetching /ServiceProviderConfig ...");
        // Test ServiceProviderConfig
        result = new TestCaseResult("ServiceProviderConfig");
        result.setRequestMethod("GET");
        try {
            spc = fetcher.fetchServiceProviderConfig();
            result.setSuccess(true);
            result.setResponseBody(spc.toString());
            result.setResponseCode(200);
            database.addTestResult(runId, result);
        } catch (Exception e) {
            result.setException(e);
            database.addTestResult(runId, result);
            return;
        }


        System.out.println("Fetching /ResourceTypes ...");
        // Test ResourceTypes
        result = new TestCaseResult("ResourceTypes");
        result.setRequestMethod("GET");
        try {
            resourceTypes = fetcher.fetchResourceTypes();
            result.setSuccess(true);
            result.setResponseBody(resourceTypes.toString());
            result.setResponseCode(200);
            database.addTestResult(runId, result);
        } catch (Exception e) {
            result.setException(e);
            database.addTestResult(runId, result);
            return;
        }


        System.out.println("Fetching /Schemas ...");
        // Test Schemas
        result = new TestCaseResult("Schemas");
        result.setRequestMethod("GET");
        try {
            schemas = fetcher.fetchSchemas(resourceTypes);
            result.setSuccess(true);
            result.setResponseCode(200);
            result.setResponseBody(schemas.toString());
            database.addTestResult(runId, result);
        } catch (Exception e) {
            result.setException(e);
            database.addTestResult(runId, result);
            return;
        }

        //System.out.println(schemas.getJsonSchema(ScimConstants.SCHEMA_USER));



        if (testContext.isUsersIncluded()) {
            System.out.println("Running Tests /Users");
            runTestsCase(runId, new UserCrudTests(testContext, spc, resourceTypes, schemas));
        }

        if (testContext.isGroupsIncluded()) {
            System.out.println("Running Tests /Groups");
            runTestsCase(runId, new GroupCrudTests(testContext, spc, resourceTypes, schemas));
        }

        if (testContext.isMeIncluded()) {
            System.out.println("Running Tests /Me");
            runTestsCase(runId, new MeTests(testContext, spc, resourceTypes, schemas));
        }

        if (testContext.isSearchIncluded()) {
            System.out.println("Running Tests /.search");
            runTestsCase(runId, new SearchTests(testContext, spc, resourceTypes, schemas));
        }

        if (testContext.isBulkIncluded()) {
            System.out.println("Running Tests /Bulk");
            runTestsCase(runId, new BulkTests(testContext, spc, resourceTypes, schemas));
        }

        System.out.println("Tests DONE!!\n");
        database.addTestResult(runId, new TestCaseResult("--DONE--"));
    }

    private void runTestsCase(String runId, TestsCase testCase) {
        List<TestCaseResult> results;
        try {
            results = testCase.execute();
        } catch (Exception e) {

            TestCaseResult errResult = new TestCaseResult("Unexpected Error");
            errResult.setException(e);

            results = new ArrayList<>();
            results.add(errResult);
        }

        database.addTestResults(runId, results);
    }
}
