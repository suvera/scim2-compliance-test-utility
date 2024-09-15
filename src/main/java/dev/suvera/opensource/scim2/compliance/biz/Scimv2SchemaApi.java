package dev.suvera.opensource.scim2.compliance.biz;

import dev.suvera.opensource.scim2.compliance.data.ScimApiResponse;
import dev.suvera.opensource.scim2.compliance.enums.HttpMethod;
import okhttp3.Response;

public class Scimv2SchemaApi extends Scimv2BaseApi {
    public static final String SCHEMAS = "/Schemas";

    public Scimv2SchemaApi(ScimApiClient client) {
        super(client);
    }

    public ScimApiResponse<String> getgetSchemasWithHttpInfo() throws ScimApiException {
        try (Response response = client.sendRequest(HttpMethod.GET, SCHEMAS, null)) {
            okhttp3.ResponseBody body = response.body();
            return new ScimApiResponse<>(response.code(), response.headers().toMultimap(), body == null ? null : body.string());
        } catch (Exception e) {
            throw new ScimApiException(e);
        }
    }
}
