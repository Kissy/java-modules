package fr.kissy.module.rest.config;

import fr.kissy.module.rest.interceptor.AuthentificationHeaderInterceptor;
import fr.kissy.module.rest.interceptor.JsonHeaderInterceptor;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Guillaume <lebiller@fullsix.com>
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    public DefaultHttpClient defaultHttpClient() {
        return new DefaultHttpClient();
    }
    @Bean
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory();
    }
    @Bean
    public AuthentificationHeaderInterceptor authentificationHeaderInterceptor() {
        return new AuthentificationHeaderInterceptor();
    }
    @Bean
    public JsonHeaderInterceptor jsonHeaderInterceptor() {
        return new JsonHeaderInterceptor();
    }
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory());
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(authentificationHeaderInterceptor());
        interceptors.add(jsonHeaderInterceptor());
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}
