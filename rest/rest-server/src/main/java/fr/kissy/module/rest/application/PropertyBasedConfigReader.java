package fr.kissy.module.rest.application;

import com.wordnik.swagger.jaxrs.ConfigReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;

public class PropertyBasedConfigReader extends ConfigReader {
    @Value("${module.rest.swagger.api.base.path}")
    private String swaggerApiBasePath;
    @Value("${module.rest.swagger.api.version}")
    private String swaggerApiVersion;
    @Value("${module.rest.swagger.api.model.package}")
    private String swaggerApiModelPackage;

    /**
     * Default Constructor.
     *
     * @param config The Servlet config.
     */
    public PropertyBasedConfigReader(ServletConfig config) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
        AutowireCapableBeanFactory factory = webApplicationContext.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String basePath() {
        return swaggerApiBasePath;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String swaggerVersion() {
        return com.wordnik.swagger.core.SwaggerSpec.version();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String apiVersion() {
        return swaggerApiVersion;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String modelPackages() {
        return swaggerApiModelPackage;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String apiFilterClassName() {
        return null;
    }
}