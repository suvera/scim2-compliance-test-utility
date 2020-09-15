package dev.suvera.opensource.scim2.compliance.tests;

import dev.suvera.opensource.scim2.compliance.data.TestCaseResult;

import java.util.List;

/**
 * author: suvera
 * date: 9/3/2020 2:22 PM
 */
public interface TestsCase {

    List<TestCaseResult> execute();
}
