package dev.suvera.opensource.scim2.compliance.biz;

import dev.suvera.opensource.scim2.compliance.data.ScimApiResponse;
import dev.suvera.opensource.scim2.compliance.enums.HttpMethod;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Scimv2ServiceProviderConfigApi extends Scimv2BaseApi {
    private final static Logger log = LogManager.getLogger(Scimv2ServiceProviderConfigApi.class);
    public static final String SERVICE_PROVIDER_CONFIG = "/ServiceProviderConfig";

    public Scimv2ServiceProviderConfigApi(ScimApiClient client) {
        super(client);
    }

    public ScimApiResponse<String> getServiceProviderConfigWithHttpInfo() throws ScimApiException {
        try  {
            if (client == null) {
                throw new ScimApiException("Client is not set");
            }
            Response response = client.sendRequest(HttpMethod.GET, SERVICE_PROVIDER_CONFIG, null);
            okhttp3.ResponseBody body = response.body();
            ScimApiResponse<String> output = new ScimApiResponse<>(response.code(), response.headers().toMultimap(), body == null ? null : body.string());
            response.close();
            return output;
        } catch (Exception e) {
            log.info("Error in getServiceProviderConfigWithHttpInfo", e);
            throw new ScimApiException(e);
        }
    }
}
