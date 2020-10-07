package dev.suvera.opensource.scim2.compliance.biz;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * author: suvera
 * date: 10/7/2020 12:54 PM
 */
public class LoggingInterceptor implements Interceptor {

    private final String purpose;

    public LoggingInterceptor(String purpose) {
        this.purpose = purpose;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        System.out.println("\nOkHttp [" + purpose + "] " +
                String.format("Sending request %s on %s%n\n%s\nRequestBody:%s",
                        request.url(), chain.connection(), request.headers(), request.body())
        );

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        System.out.println("\nOkHttp [" + purpose + "] " +
                String.format("Received response for %s in %.1fms%n\n%s",
                        response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}
