package fr.kissy.module.rest.config;

import fr.kissy.module.rest.resource.ApiListingResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Guillaume Le Biller
 */
@Configuration
public abstract class AbstractResourceConfig {
    @Bean
    public ApiListingResource apiListingResource() {
        return new ApiListingResource();
    }
}
