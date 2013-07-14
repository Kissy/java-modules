package fr.kissy.module.rest.interceptor;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;
import java.util.Collections;

public class AuthentificationHeaderInterceptor implements ClientHttpRequestInterceptor {
    @Value("${module.rest.client.auth.token:}")
    private String authToken;
    @Value("${module.rest.client.auth.user:}")
    private String authUser;
    @Value("${module.rest.client.auth.password:}")
    private String authPassword;

    /**
     * @inheritDoc
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (StringUtils.isNotBlank(authToken)) {
            request.getHeaders().add("Authorization", "Basic " + Base64.encodeBase64String(authToken.getBytes()));
        }
        if (StringUtils.isNotBlank(authUser) || StringUtils.isNotBlank(authPassword)) {
            String auth = authUser + ':' + authPassword;
            request.getHeaders().add("Authorization", "Basic " + Base64.encodeBase64String(auth.getBytes()));
        }
        return execution.execute(request, body);
    }
}