package fr.kissy.module.rest.application;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public abstract class AbstractJavaApplicationInitializer implements WebApplicationInitializer {
    /**
     * @inheritDoc
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(getJavaConfiguration());
        servletContext.addListener(new ContextLoaderListener(applicationContext));

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(CXFServlet.class.getSimpleName(), CXFServlet.class);
        dispatcher.setInitParameter("swagger.config.reader", PropertyBasedConfigReader.class.getName());
        dispatcher.addMapping("/rest/*");
        dispatcher.setLoadOnStartup(1);
    }

    /**
     * Get the Java Configuration Class to Bootstrap.
     *
     * @return The Java Configuration Class to bootstrap.
     */
    protected abstract Class getJavaConfiguration();
}