package dev.suvera.opensource.scim2.compliance.biz;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.suvera.opensource.scim2.compliance.enums.HttpMethod;
import dev.suvera.opensource.scim2.compliance.utils.UrlUtil;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ScimApiClient {
    private final static Logger log = LogManager.getLogger(ScimApiClient.class);
    public final static String CLIENT_NAME = "scim2-compliance-client-1.0";
    public static final String CONTENT_TYPE = "Content-Type";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String scimApiUrl;
    private OkHttpClient client;
    private String endPoint;

    public ScimApiClient(String scimApiUrl, OkHttpClient client) {
        this.scimApiUrl = scimApiUrl;
        this.client = client;
    }

    public void setURL(String url) {
        scimApiUrl = url;
    }

    public void setServicePath(String endPoint) {
        this.endPoint = endPoint;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getScimApiUrl() {
        return scimApiUrl;
    }

    public void setScimApiUrl(String scimApiUrl) {
        this.scimApiUrl = scimApiUrl;
    }

    public Response sendRequest(
            HttpMethod method,
            String path,
            Object payload
    ) throws ScimApiException {

        String url = scimApiUrl;
        if (endPoint != null) {
            if (!url.endsWith("/")) {
                url += "/";
            }
            String endPoint = StringUtils.stripStart(this.endPoint, " /");
            url += endPoint;
        }
        if (path != null) {
            path = StringUtils.stripStart(path, " /");
            path = "/" + path;
            url += path;
        }
        if (HttpMethod.GET.equals(method)) {
            String query = UrlUtil.queryString(payload);
            if (query != null) {
                url += "?" + query;
            }
        }
        log.info("Http {} request {}", method, url);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .header("X-Requested-With", CLIENT_NAME);

        String body = null;
        if (payload != null) {
            if (payload instanceof String) {
                body = payload.toString();
            } else {
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                try {
                    body = objectMapper.writeValueAsString(payload);
                } catch (JsonProcessingException e) {
                    throw new ScimApiException("Failed while encoding object to JSON", e);
                }
            }
        }

        if (HttpMethod.DELETE.equals(method)) {
            if (body != null && !body.isEmpty()) {
                builder.delete(RequestBody.create(body, MediaType.parse("application/scim+json")));
            } else {
                builder.header(CONTENT_TYPE, "application/scim+json").delete();
            }
        } else if (HttpMethod.PUT.equals(method)) {
            if (body == null) {
                body = "";
            }
            builder.put(RequestBody.create(body, MediaType.parse("application/scim+json")));
        } else if (HttpMethod.PATCH.equals(method)) {
            if (body == null) {
                body = "";
            }
            builder.patch(RequestBody.create(body, MediaType.parse("application/scim+json")));
        } else if (!HttpMethod.GET.equals(method)) {
            if (body == null) {
                body = "";
            }
            builder.post(RequestBody.create(body, MediaType.parse("application/scim+json")));
        }

        Request request = builder.build();
        Call call = client.newCall(request);
        Response response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new ScimApiException("Could not send HTTP request to scim2 service", e);
        }

        return response;
    }
}
