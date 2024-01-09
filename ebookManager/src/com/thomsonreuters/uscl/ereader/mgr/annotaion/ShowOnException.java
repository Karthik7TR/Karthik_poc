package com.thomsonreuters.uscl.ereader.mgr.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;

/**
 * Annotation provides an ability to return specific ModelAndView in case of exception occurred while request handling.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ShowOnException {
    String errorViewName() default StringUtils.EMPTY;
    String errorRedirectMvcName() default StringUtils.EMPTY;
}
