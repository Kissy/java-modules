package fr.kissy.module.rest.config;

import fr.kissy.module.rest.handler.SpringTemplateResourceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

/**
 * @author Guillaume Le Biller
 */
@Configuration
public abstract class AbstractResourceConfig {
    @Bean
    public SpringTemplateResourceHandler springTemplateResourceHandler() {
        return new SpringTemplateResourceHandler();
    }

    protected Object getResourceHandlerProxy(Class clazz) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{clazz}, springTemplateResourceHandler());
    }
}
