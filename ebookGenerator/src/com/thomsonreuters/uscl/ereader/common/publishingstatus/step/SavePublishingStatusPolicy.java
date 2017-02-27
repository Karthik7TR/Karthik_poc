package com.thomsonreuters.uscl.ereader.common.publishingstatus.step;

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
public @interface SavePublishingStatusPolicy
{
    StatsUpdateTypeEnum[] value() default StatsUpdateTypeEnum.GENERAL;
}
