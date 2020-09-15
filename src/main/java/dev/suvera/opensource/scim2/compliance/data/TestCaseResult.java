package dev.suvera.opensource.scim2.compliance.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: suvera
 * date: 9/3/2020 2:23 PM
 */
@Data
@NoArgsConstructor
public class TestCaseResult {
    private boolean success = false;
    private boolean notSupported = false;

    private String title = "";

    private String requestBody = "";
    private String requestMethod = "";

    private String responseBody = "";
    private int responseCode;
    private Map<String, List<String>> responseHeaders = new HashMap<>();

    private Exception exception;

    public TestCaseResult(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "\nTestCaseResult{" +
                "\n\t  success=" + success +
                "\n\t  notSupported=" + notSupported +
                "\n\t, title='" + title + '\'' +
                "\n\t, requestBody='" + requestBody + '\'' +
                "\n\t, requestMethod='" + requestMethod + '\'' +
                "\n\t, responseBody='" + responseBody + '\'' +
                "\n\t, responseCode=" + responseCode +
                "\n\t, responseHeaders=" + responseHeaders +
                "\n\t, exception=" + ( exception != null ? ExceptionUtils.getFullStackTrace(exception) : "null\n") +
                '}';
    }
}
