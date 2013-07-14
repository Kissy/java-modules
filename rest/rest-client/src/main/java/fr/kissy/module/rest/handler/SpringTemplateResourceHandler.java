package fr.kissy.module.rest.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guillaume <lebiller@fullsix.com>
 */
public class SpringTemplateResourceHandler implements InvocationHandler {
    @Value("${module.rest.client.base.url}")
    private String baseUrl;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            Object entity = null;
            URIBuilder uriBuilder = new URIBuilder(baseUrl);
            uriBuilder.setPath(uriBuilder.getPath() + toPath(method));

            // Build the URL based on some of the JAX-RS annotations
            Annotation httpMethod = findHttpMethod(method);
            Annotation[][] allParameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < allParameterAnnotations.length; i++) {
                Annotation[] parameterAnnotations = allParameterAnnotations[i];
                Object arg = args[i];

                if (parameterAnnotations.length == 0) {
                    entity = arg;
                    continue;
                }

                for (Annotation parameterAnnotation : parameterAnnotations) {
                    if (parameterAnnotation instanceof PathParam) {
                        String pathParameter = '{' + ((PathParam) parameterAnnotation).value() + '}';
                        uriBuilder.setPath(StringUtils.replace(uriBuilder.getPath(), pathParameter, arg.toString()));
                    } else if (parameterAnnotation instanceof QueryParam) {
                        uriBuilder.setParameter(((QueryParam) parameterAnnotation).value(), arg.toString());
                    } else if (parameterAnnotation instanceof FormParam) {
                        entity = arg;
                    }
                }
            }

            if (httpMethod instanceof POST) {
                return restTemplate.postForObject(uriBuilder.build(), entity, method.getReturnType());
            } else if (httpMethod instanceof GET) {
                return restTemplate.getForObject(uriBuilder.build(), method.getReturnType());
            } else if (httpMethod instanceof DELETE) {
                restTemplate.delete(uriBuilder.build());
            } else if (httpMethod instanceof PUT) {
                restTemplate.put(uriBuilder.build(), entity);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Annotation findHttpMethod(Method m) {
        List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();
        annotations.add(POST.class);
        annotations.add(GET.class);
        annotations.add(PUT.class);
        annotations.add(DELETE.class);

        // First, search the supplied method for the HTTP method annotations
        for (Class<? extends Annotation> candidate : annotations) {
            Annotation annotation = m.getAnnotation(candidate);
            if (annotation != null) {
                return annotation;
            }
        }

        // Finally, search the supplied method's declaring class for the HTTP method annotations
        for (Class<? extends Annotation> candidate : annotations) {
            Annotation annotation = m.getDeclaringClass().getAnnotation(candidate);
            if (annotation != null) {
                return annotation;
            }
        }

        return null;
    }

    private String toPath(Method m) {
        StringBuilder pathBuilder = new StringBuilder();
        Path path = m.getDeclaringClass().getAnnotation(Path.class);
        if (path != null) {
            pathBuilder.append(path.value());
        }
        Path subPath = m.getAnnotation(Path.class);
        if (subPath != null) {
            pathBuilder.append(subPath.value());
        }
        return pathBuilder.toString();
    }
}
