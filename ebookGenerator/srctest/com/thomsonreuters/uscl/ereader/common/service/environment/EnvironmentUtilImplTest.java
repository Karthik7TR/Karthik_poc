package com.thomsonreuters.uscl.ereader.common.service.environment;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class EnvironmentUtilImplTest
{
    @InjectMocks
    private EnvironmentUtilImpl util;

    @Test
    public void shouldReturnTrueIfProd() throws IllegalAccessException
    {
        //given
        FieldUtils.writeField(util, "environmentName", "prodcontent", true);
        //when
        final boolean prod = util.isProd();
        //then
        assertThat(prod, is(true));
    }

    @Test
    public void shouldReturnFalseIfNotProd() throws IllegalAccessException
    {
        //given
        FieldUtils.writeField(util, "environmentName", "notprod", true);
        //when
        final boolean prod = util.isProd();
        //then
        assertThat(prod, is(false));
    }
}
