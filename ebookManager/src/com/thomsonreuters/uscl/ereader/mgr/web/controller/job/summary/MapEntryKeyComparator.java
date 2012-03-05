/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.util.Comparator;
import java.util.Map;

public class MapEntryKeyComparator implements Comparator<Map.Entry<String,?>> {
	public int compare(Map.Entry<String,?> o1, Map.Entry<String,?> o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			} else {
				return -1;
			}
		} else { // (o1 != null)
			if (o2 == null) {
				return 1;
			} else {  // (o2 != null)
				return o1.getKey().compareTo(o2.getKey());
			}
		}
	}
}
