package com.thomsonreuters.uscl.ereader.common.retry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.common.retry.infrastructure.RetryMetadata;
import com.thomsonreuters.uscl.ereader.common.retry.infrastructure.RetryMethodInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Infrastructure bean, proxying beans with methods annotated with @Retry
 */
public class RetryAwareBeanPostProcessor implements BeanPostProcessor {
    private final Map<String, List<RetryMetadata>> retryMethodsData = new HashMap<>();
    private final Properties commonProperties;

    public RetryAwareBeanPostProcessor(final Properties commonProperties) {
        this.commonProperties = commonProperties;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        Object resultBean = bean;
        final List<RetryMetadata> retryMethodsDataList = retryMethodsData.get(beanName);
        if (retryMethodsDataList != null) {
            final ProxyFactory factory = new ProxyFactory(bean);
            factory.addAdvice(new RetryMethodInterceptor(retryMethodsDataList));
            resultBean = factory.getProxy();
        }
        return resultBean;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        Stream.of(bean.getClass().getMethods())
            .filter(method -> method.isAnnotationPresent(Retry.class))
            .forEach(method -> {
                final Retry annotation = method.getAnnotation(Retry.class);
                final List<RetryMetadata> methodsMetadata = getMethodsData(beanName);
                methodsMetadata.add(new RetryMetadata(method.getName(), getRetriesValue(annotation),
                    annotation.exceptions(), getDelayValue(annotation),
                    annotation.timeUnit()));
            });

        return bean;
    }

    private List<RetryMetadata> getMethodsData(final String beanName) {
        if (!retryMethodsData.containsKey(beanName)) {
            retryMethodsData.put(beanName, new ArrayList<>());
        }
        return retryMethodsData.get(beanName);
    }

    private int getRetriesValue(final Retry annotation) {
        return getPropertyValue(annotation.propertyValue(), annotation.value(), Integer::valueOf);
    }

    private long getDelayValue(final Retry annotation) {
        return getPropertyValue(annotation.delayProperty(), annotation.delay(), Long::valueOf);
    }

    private <T extends Number> T getPropertyValue(final String property, final T value, final Function<String, T> castFunction) {
        final T result;
        if (StringUtils.isNotBlank(property)) {
            result = Optional.of(property)
                .map(commonProperties::getProperty)
                .map(castFunction)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Property %s not found", property)));
        } else {
            result = value;
        }
        return result;
    }
}
