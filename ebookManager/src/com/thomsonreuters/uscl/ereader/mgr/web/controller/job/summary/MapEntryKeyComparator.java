package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.util.Comparator;
import java.util.Map;

public class MapEntryKeyComparator implements Comparator<Map.Entry<String, ?>> {
    @Override
    public int compare(final Map.Entry<String, ?> o1, final Map.Entry<String, ?> o2) {
        if (o1 == null) {
            if (o2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else { // (o1 != null)
            if (o2 == null) {
                return 1;
            } else { // (o2 != null)
                return o1.getKey().compareTo(o2.getKey());
            }
        }
    }
}
