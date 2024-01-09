package com.thomsonreuters.uscl.ereader.mgr.annotaion.infrastructure;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Infrastructure bean, proxying beans with methods annotated with @ShowOnException
 */
@Component
public class AnnotationAwareBeanPostProcessor implements BeanPostProcessor {
    private final Map<String, List<AnnotationMetadata>> targetMethodsData = new HashMap<>();
    private static final String PARAMETERS_ERROR_MESSAGE =
        "%s.%s: it is not expected that parameters errorViewName and errorRedirectMvcName of annotation @ViewOnException are both%s empty.";

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) {
        Object resultBean = bean;
        final List<AnnotationMetadata> retryMethodsDataList = targetMethodsData.get(beanName);
        if (retryMethodsDataList != null) {
            final ProxyFactory factory = new ProxyFactory(bean);
            factory.addAdvice(new AnnotatedMethodInterceptor(retryMethodsDataList));
            resultBean = factory.getProxy();
        }
        return resultBean;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) {
        Stream.of(bean.getClass().getMethods())
            .filter(method -> method.isAnnotationPresent(ShowOnException.class))
            .forEach(method -> {
                final ShowOnException annotation = method.getAnnotation(ShowOnException.class);
                final List<AnnotationMetadata> methodsMetadata = targetMethodsData.computeIfAbsent(beanName, key -> new ArrayList<>());
                methodsMetadata.add(new AnnotationMetadata(method.getName(), annotation.errorViewName(), annotation.errorRedirectMvcName()));
                validateAnnotationParameters(annotation, bean, method);
            });
        return bean;
    }

    private void validateAnnotationParameters(final ShowOnException annotation, final Object bean, final Method method) {
        if (StringUtils.isEmpty(annotation.errorViewName()) && StringUtils.isEmpty(annotation.errorRedirectMvcName())) {
            throw new IllegalArgumentException(String.format(PARAMETERS_ERROR_MESSAGE, bean.getClass(), method.getName(), ""));
        }
        if (StringUtils.isNotEmpty(annotation.errorViewName()) && StringUtils.isNotEmpty(annotation.errorRedirectMvcName())) {
            throw new IllegalArgumentException(String.format(PARAMETERS_ERROR_MESSAGE, bean.getClass(), method.getName(), " not"));
        }
    }
}
