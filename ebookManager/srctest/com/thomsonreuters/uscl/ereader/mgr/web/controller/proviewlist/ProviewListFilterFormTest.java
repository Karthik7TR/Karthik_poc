package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import org.junit.Test;
import static org.junit.Assert.assertFalse;

public class ProviewListFilterFormTest {
    private static final String FINAL_STATUS = "Final";

    @Test
    public void testAreAllFiltersBlank() {
        ProviewListFilterForm form = new ProviewListFilterForm();
        form.setStatus(FINAL_STATUS);
        assertFalse(form.areAllFiltersBlank());
    }
}