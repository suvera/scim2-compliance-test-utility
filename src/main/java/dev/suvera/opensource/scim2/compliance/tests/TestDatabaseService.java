package dev.suvera.opensource.scim2.compliance.tests;

import dev.suvera.opensource.scim2.compliance.data.TestCaseResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: suvera
 * date: 9/7/2020 5:11 PM
 */
@Service
public class TestDatabaseService {
    private static final Map<String, List<TestCaseResult>> tests = new ConcurrentHashMap<>();

    public void addTestResult(String runId, TestCaseResult result) {
        if (!tests.containsKey(runId)) {
            tests.put(runId, new ArrayList<>());
        }

        tests.get(runId).add(result);
    }

    public void addTestResults(String runId, List<TestCaseResult> results) {
        for (TestCaseResult result : results) {
            addTestResult(runId, result);
        }
    }

    public List<TestCaseResult> getTestResults(String runId) {
        if (!tests.containsKey(runId)) {
            return Collections.emptyList();
        }

        return tests.get(runId);
    }
}
