package dev.suvera.opensource.scim2.compliance.data;


import java.util.List;
import java.util.Map;

public class ScimApiResponse<T> {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final T data;

    public ScimApiResponse(int statusCode, Map<String, List<String>> headers) {
        this(statusCode, headers, (T)null);
    }

    public ScimApiResponse(int statusCode, Map<String, List<String>> headers, T data) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.data = data;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public T getData() {
        return this.data;
    }
}