package dev.suvera.opensource.scim2.compliance.biz;

import dev.suvera.opensource.scim2.compliance.data.ScimApiResponse;
import dev.suvera.opensource.scim2.compliance.enums.HttpMethod;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

abstract public class Scimv2BaseApi {
    private final static Logger log = LogManager.getLogger(Scimv2BaseApi.class);
    protected ScimApiClient client;

    public Scimv2BaseApi(ScimApiClient client) {
        this.client = client;
    }

    public ScimApiClient getScimApiClient() {
        return client;
    }

    public ScimApiResponse<String> updateWithHttpInfo(String reqBody, HttpMethod method) throws ScimApiException {
        try (Response response = client.sendRequest(method, null, reqBody)) {
            okhttp3.ResponseBody body = response.body();
            return new ScimApiResponse<>(response.code(), response.headers().toMultimap(), body == null ? null : body.string());
        } catch (Exception e) {
            throw new ScimApiException(e);
        }
    }

    public ScimApiResponse<String> createWithHttpInfo(String reqBody) throws ScimApiException {
        log.info("Creating record with body: {}", reqBody);
        try (Response response = client.sendRequest(HttpMethod.POST, null, reqBody)) {
            okhttp3.ResponseBody body = response.body();
            return new ScimApiResponse<>(response.code(), response.headers().toMultimap(), body == null ? null : body.string());
        } catch (Exception e) {
            throw new ScimApiException(e);
        }
    }

    public ScimApiResponse<String> deleteWithHttpInfo() throws ScimApiException {
        try (Response response = client.sendRequest(HttpMethod.DELETE, null, null)) {
            okhttp3.ResponseBody body = response.body();
            return new ScimApiResponse<>(response.code(), response.headers().toMultimap(), body == null ? null : body.string());
        } catch (Exception e) {
            throw new ScimApiException(e);
        }
    }

    public ScimApiResponse<String> getRecordByIdWithHttpInfo() throws ScimApiException {
        try (Response response = client.sendRequest(HttpMethod.GET, null, null)) {
            okhttp3.ResponseBody body = response.body();
            return new ScimApiResponse<>(response.code(), response.headers().toMultimap(), body == null ? null : body.string());
        } catch (Exception e) {
            throw new ScimApiException(e);
        }
    }

    public ScimApiResponse<String> getSearchWithHttpInfo(String filter, Integer startIndex, Integer count, String sortBy, String sortOrder) throws ScimApiException {
        Map<String, Object> queryMap = new HashMap<>();
        if (filter != null) {
            queryMap.put("filter", filter);
        }
        if (startIndex != null) {
            queryMap.put("startIndex", startIndex);
        }
        if (count != null) {
            queryMap.put("count", count);
        }
        if (sortBy != null) {
            queryMap.put("sortBy", sortBy);
        }
        if (sortOrder != null) {
            queryMap.put("sortOrder", sortOrder);
        }

        try (Response response = client.sendRequest(HttpMethod.GET, null, queryMap.isEmpty() ? null : queryMap)) {
            okhttp3.ResponseBody body = response.body();
            return new ScimApiResponse<>(response.code(), response.headers().toMultimap(), body == null ? null : body.string());
        } catch (Exception e) {
            throw new ScimApiException(e);
        }
    }

}
