package dev.suvera.opensource.scim2.compliance.biz;

import com.squareup.okhttp.Interceptor;
import dev.suvera.opensource.scim2.compliance.data.TestContext;
import dev.suvera.opensource.scim2.compliance.enums.AuthenticationType;
import io.scim2.swagger.client.ScimApiClient;
import io.scim2.swagger.client.ScimApiException;
import io.scim2.swagger.client.api.*;
import lombok.Data;

import java.util.List;

/**
 * author: suvera
 * date: 9/6/2020 11:25 AM
 */
@SuppressWarnings("WeakerAccess")
@Data
public class ScimApiClientBuilder {
    private final TestContext testContext;

    public ScimApiClientBuilder(TestContext testContext) {
        this.testContext = testContext;
    }

    public ScimApiClient client(String endPoint) throws ScimApiException {
        ScimApiClient client = new ScimApiClient();
        client.setConnectTimeout(1200000);
        client.setReadTimeout(1200000);

        client.setURL(testContext.getEndPoint());
        if (endPoint != null) {
            client.setServicePath(endPoint);
        }
        if (AuthenticationType.BEARER.equals(testContext.getAuthType())) {
            client.setAccessToken(testContext.getBearerToken());
        } else {
            client.setUsername(testContext.getUserName());
            client.setPassword(testContext.getPassword());
        }

        List<Interceptor> appInterceptors = client.getHttpClient().interceptors();
        appInterceptors.add(new LoggingInterceptor("APP"));

        List<Interceptor> netInterceptors = client.getHttpClient().networkInterceptors();
        netInterceptors.add(new LoggingInterceptor("NETWORK"));

        return client;
    }

    public Scimv2ServiceProviderConfigApi getServiceProviderConfigClient() throws ScimApiException {
        return new Scimv2ServiceProviderConfigApi(client(null));
    }

    public Scimv2ResourceTypeApi getResourceTypeClient() throws ScimApiException {
        return new Scimv2ResourceTypeApi(client(null));
    }

    public Scimv2SchemaApi getScimv2SchemClient() throws ScimApiException {
        return new Scimv2SchemaApi(client(null));
    }

    public Scimv2UsersApi getScimv2UsersClient(String endPoint) throws ScimApiException {
        return new Scimv2UsersApi(client(endPoint));
    }

    public Scimv2GroupsApi getScimv2GroupsClient(String endPoint) throws ScimApiException {
        return new Scimv2GroupsApi(client(endPoint));
    }
}
