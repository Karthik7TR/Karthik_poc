/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.util;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;

@RunWith(MockitoJUnitRunner.class)
public class VersionUtilImplTest {
    @InjectMocks
    private VersionUtilImpl util;

    @Test
    public void shouldReturnTrueForMajorUpdate() throws Exception {
        // given
        Version current = version("v1.1");
        Version next = version("v2.0");
        // when
        boolean majorUpdate = util.isMajorUpdate(current, next);
        // then
        assertThat(majorUpdate, is(true));
    }

    @Test
    public void shouldReturnFalseForMinorUpdate() throws Exception {
        // given
        Version current = version("v1.1");
        Version next = version("v1.2");
        // when
        boolean majorUpdate = util.isMajorUpdate(current, next);
        // then
        assertThat(majorUpdate, is(false));
    }
}
