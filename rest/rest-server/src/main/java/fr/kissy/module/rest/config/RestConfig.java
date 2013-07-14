package fr.kissy.module.rest.config;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.collect.Lists;
import com.wordnik.swagger.jaxrs.JaxrsApiReader;
import fr.kissy.module.rest.application.WebServiceScanningApplication;
import fr.kissy.module.rest.mapper.CustomExceptionMapper;
import fr.kissy.module.rest.mapper.WebApplicationExceptionMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.model.wadl.WadlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.jws.WebService;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

/**
 * @author Guillaume Le Biller (<i>lebiller@ekino.com</i>)
 * @version $Id$
 */
@Configuration
@ImportResource("classpath:META-INF/cxf/cxf.xml")
public class RestConfig {
    static {
        JaxrsApiReader.setFormatString(StringUtils.EMPTY);
    }

    @Value("${module.rest.server.address}")
    private String serverAddress;
    @Value("${module.rest.wadl.namespace.prefix}")
    private String wadlNamespacePrefix;
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public WebServiceScanningApplication basicApplication() {
        return new WebServiceScanningApplication();
    }
    @Bean
    public JacksonJaxbJsonProvider jacksonJsonProvider() {
        return new JacksonJaxbJsonProvider();
    }
    @Bean
    public CustomExceptionMapper customExceptionMapper() {
        return new CustomExceptionMapper();
    }
    @Bean
    public WebApplicationExceptionMapper webApplicationExceptionMapper() {
        return new WebApplicationExceptionMapper();
    }
    @Bean
    public WadlGenerator wadlGenerator() {
        WadlGenerator wadlGenerator = new WadlGenerator();
        wadlGenerator.setLinkJsonToXmlSchema(true);
        wadlGenerator.setNamespacePrefix(wadlNamespacePrefix);
        return wadlGenerator;
    }
    @Bean
    public List<?> providers() {
        return Lists.newArrayList(jacksonJsonProvider(), customExceptionMapper(),
                webApplicationExceptionMapper(), wadlGenerator());
    }

    @Bean(initMethod = "create")
    public JAXRSServerFactoryBean jaxrsServerFactoryBean() {
        Map<String,Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Path.class);
        JAXRSServerFactoryBean jaxrsServerFactoryBean = new JAXRSServerFactoryBean();
        jaxrsServerFactoryBean.setBus(applicationContext.getBean(SpringBus.class));
        jaxrsServerFactoryBean.setServiceBeans(Lists.newArrayList(beansWithAnnotation.values()));
        jaxrsServerFactoryBean.setApplication(basicApplication());
        jaxrsServerFactoryBean.setProviders(providers());
        jaxrsServerFactoryBean.setAddress(serverAddress);
        return jaxrsServerFactoryBean;
    }
}
