package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;

/**
 *  Marks how to save publishing status for step
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SavePublishingStatusStrategy
{
    StatsUpdateTypeEnum value() default StatsUpdateTypeEnum.GENERAL;
}
