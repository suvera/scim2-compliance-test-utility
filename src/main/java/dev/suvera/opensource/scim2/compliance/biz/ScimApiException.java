package dev.suvera.opensource.scim2.compliance.biz;

import java.util.List;
import java.util.Map;

public class ScimApiException extends Exception {
    private int code;
    private Map<String, List<String>> responseHeaders;
    private String responseBody;

    public ScimApiException() {
        this.code = 0;
        this.responseHeaders = null;
        this.responseBody = null;
    }

    public ScimApiException(Throwable throwable) {
        super(throwable);
        this.code = 0;
        this.responseHeaders = null;
        this.responseBody = null;
    }

    public ScimApiException(String message) {
        super(message);
        this.code = 0;
        this.responseHeaders = null;
        this.responseBody = null;
    }

    public ScimApiException(String message, Throwable throwable) {
        super(message, throwable);
        this.code = 0;
        this.responseHeaders = null;
        this.responseBody = null;
    }

    public ScimApiException(String message, Throwable throwable, int code, Map<String, List<String>> responseHeaders, String responseBody) {
        super(message, throwable);
        this.code = 0;
        this.responseHeaders = null;
        this.responseBody = null;
        this.code = code;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public ScimApiException(String message, int code, Map<String, List<String>> responseHeaders, String responseBody) {
        this(message, (Throwable) null, code, responseHeaders, responseBody);
    }

    public ScimApiException(String message, Throwable throwable, int code, Map<String, List<String>> responseHeaders) {
        this(message, throwable, code, responseHeaders, (String) null);
    }

    public ScimApiException(int code, Map<String, List<String>> responseHeaders, String responseBody) {
        this((String) null, (Throwable) null, code, responseHeaders, responseBody);
    }

    public ScimApiException(int code, String message) {
        super(message);
        this.code = 0;
        this.responseHeaders = null;
        this.responseBody = null;
        this.code = code;
    }

    public ScimApiException(int code, String message, Map<String, List<String>> responseHeaders, String responseBody) {
        this(code, message);
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public int getCode() {
        return this.code;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return this.responseHeaders;
    }

    public String getResponseBody() {
        return this.responseBody;
    }
}
