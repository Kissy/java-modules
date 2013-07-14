package fr.kissy.module.rest.application;

import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.Api;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author Guillaume Le Biller
 */
public class WebServiceScanningApplication extends Application {
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = Sets.newHashSet();
        Collection<Object> beans = applicationContext.getBeansWithAnnotation(Path.class).values();
        for (Object bean : beans) {
            Class<?> targetClass = AopUtils.isJdkDynamicProxy(bean) ? ((Advised) bean).getTargetClass() : bean.getClass();
            classes.add(targetClass);
            Collections.addAll(classes, targetClass.getInterfaces());
        }
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return Collections.emptySet();
    }
}