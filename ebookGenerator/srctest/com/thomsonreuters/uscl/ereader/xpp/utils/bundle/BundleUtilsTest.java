package com.thomsonreuters.uscl.ereader.xpp.utils.bundle;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class BundleUtilsTest {
    @Mock
    private XppBundle xppBundle;

    @Test
    public void testIsPocketPart() {
        //given
        given(xppBundle.getProductType()).willReturn("supp");
        //when
        final boolean result = BundleUtils.isPocketPart(xppBundle);
        //then
        assertThat(result, equalTo(true));
    }

    @Test
    public void testIsNotPocketPart() {
        //given
        given(xppBundle.getProductType()).willReturn("bound");
        //when
        final boolean result = BundleUtils.isPocketPart(xppBundle);
        //then
        assertThat(result, equalTo(false));
    }
}
