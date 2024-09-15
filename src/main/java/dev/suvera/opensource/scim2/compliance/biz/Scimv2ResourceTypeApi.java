package dev.suvera.opensource.scim2.compliance.biz;

import dev.suvera.opensource.scim2.compliance.data.ScimApiResponse;
import dev.suvera.opensource.scim2.compliance.enums.HttpMethod;
import okhttp3.Response;

public class Scimv2ResourceTypeApi extends Scimv2BaseApi {
    public static final String RESOURCE_TYPES = "/ResourceTypes";
    public Scimv2ResourceTypeApi(ScimApiClient client) {
        super(client);
    }

    public ScimApiResponse<String> getResourceTypesWithHttpInfo() throws ScimApiException {
        try (Response response = client.sendRequest(HttpMethod.GET, RESOURCE_TYPES, null)) {
            okhttp3.ResponseBody body = response.body();
            return new ScimApiResponse<>(response.code(), response.headers().toMultimap(), body == null ? null : body.string());
        } catch (Exception e) {
            throw new ScimApiException(e);
        }
    }
}
