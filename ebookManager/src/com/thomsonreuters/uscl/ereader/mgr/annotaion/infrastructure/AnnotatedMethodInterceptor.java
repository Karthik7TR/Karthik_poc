package com.thomsonreuters.uscl.ereader.mgr.annotaion.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
public class AnnotatedMethodInterceptor implements MethodInterceptor {
    private final Map<String, AnnotationMetadata> methodsMetadata;

    public AnnotatedMethodInterceptor(final List<AnnotationMetadata> methodsMetadataList) {
        methodsMetadata = methodsMetadataList.stream()
            .collect(Collectors.toMap(AnnotationMetadata::getMethodName, Function.identity()));
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final AnnotationMetadata methodData = methodsMetadata.get(invocation.getMethod().getName());
        return methodData == null ? invocation.proceed() : invoke(invocation, methodData.getErrorViewName(), methodData.getErrorRedirectMvcName());
    }

    private Object invoke(final MethodInvocation invocation, final String errorViewName, final String errorRedirectMvcName) {
        try {
            return invocation.proceed();
        } catch (final Throwable e) {
            log.error(e.getMessage(), e);

            for (final Object argument : invocation.getArguments()) {
                if (argument instanceof Model) {
                    final Model model = (Model) argument;
                    model.addAttribute(WebConstants.KEY_ERROR_OCCURRED, Boolean.TRUE);
                }
            }
            return StringUtils.isNotEmpty(errorViewName) ? new ModelAndView(errorViewName) : new ModelAndView(new RedirectView(errorRedirectMvcName));
        }
    }
}
