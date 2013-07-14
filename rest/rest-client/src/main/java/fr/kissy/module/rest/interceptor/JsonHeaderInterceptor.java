package fr.kissy.module.rest.interceptor;

import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;
import java.util.Collections;

public class JsonHeaderInterceptor implements ClientHttpRequestInterceptor {
    /**
     * @inheritDoc
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
        request.getHeaders().setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return execution.execute(requestWrapper, body);
    }
}