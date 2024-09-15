package dev.suvera.opensource.scim2.compliance.biz;

import dev.suvera.opensource.scim2.compliance.data.TestContext;
import dev.suvera.opensource.scim2.compliance.enums.AuthenticationType;
import lombok.Data;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * author: suvera
 * date: 9/6/2020 11:25 AM
 */
@SuppressWarnings("WeakerAccess")
@Data
public class ScimApiClientBuilder {
    private final TestContext testContext;
    private static final boolean DEBUG = false;

    public ScimApiClientBuilder(TestContext testContext) {
        this.testContext = testContext;
    }

    public ScimApiClient client(String endPoint) throws ScimApiException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(1200000, TimeUnit.SECONDS)
                .readTimeout(1200000, TimeUnit.SECONDS);

        builder.networkInterceptors().add(new LoggingInterceptor("NETWORK"));
        builder.interceptors().add(new LoggingInterceptor("APP"));

        if (AuthenticationType.BEARER.equals(testContext.getAuthType())) {
            builder.authenticator(new BearerAuthenticator(testContext.getBearerToken()));
        } else {
            builder.authenticator(new BasicAuthenticator(testContext.getUserName(), testContext.getPassword()));
        }

        try {
            TrustManager trustAll = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = new TrustManager[]{trustAll};

            sslContext.init(null, trustManagers, new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0]);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ScimApiClient client = new ScimApiClient(testContext.getEndPoint(), builder.build());
        if (endPoint != null) {
            client.setEndPoint(endPoint);
        }
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

    private static class BasicAuthenticator implements Authenticator {
        private final String username;
        private final String password;

        public BasicAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Nullable
        @Override
        public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
            String credential = Credentials.basic(username, password);
            return response.request()
                    .newBuilder()
                    .header("Authorization", credential).build();
        }
    }

    @SuppressWarnings("RedundantThrows")
    private static class BearerAuthenticator implements Authenticator {
        private final String token;

        public BearerAuthenticator(String token) {
            this.token = token;
        }

        @Nullable
        @Override
        public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
        }
    }
}
