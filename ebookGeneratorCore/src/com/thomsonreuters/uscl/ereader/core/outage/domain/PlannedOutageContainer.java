/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.outage.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


/**
 * Container and convenience methods for all the planned outage events.
 * This container is initially loaded for current or future outages as each generator instance starts.
 * It is updated via a push from the manager application when the operator adds/updates/deletes on the fly. 
 */
public class PlannedOutageContainer {
	//private static final Logger log = LogManager.getLogger(PlannedOutageContainer.class);

	private Collection<PlannedOutage> outages = new ArrayList<PlannedOutage>();
	
	public PlannedOutageContainer() {
		super();
	}
	
	/**
	 * Add a new outage to the collection, removing any existing outage 
	 * object that already has the same key.
	 * @param outageToSave the new outage.
	 */
	public synchronized void add(PlannedOutage outageToSave) {
		remove(outageToSave);
		outages.add(outageToSave);
	}

	public synchronized void addAll(Collection<PlannedOutage> allOutages) {
		for (PlannedOutage outage : allOutages) {
			add(outage);
		}
	}
	
	/**
	 * Finds all outages in effect for the specified point in time.
	 * Time comparison in inclusive of the start time, and exclusive of the end time.
	 * @param timeInstant the time instant to check.
	 * @return the outage at the specified time, or null if none found
	 */
	public PlannedOutage findOutage(Date timeInstant) {
		for (PlannedOutage outage : outages) {
			if (outage.isActive(timeInstant)) {
				return outage;
			}
		}
		return null;
	}

	public synchronized PlannedOutage findExpiredOutage(Date checkTime) {
		for (PlannedOutage outage : outages) {
			if (checkTime.after(outage.getEndTime())) {
				return outage;
			}
		}
		return null;
	}
	
	/**
	 * Remove an outage from the collection by its key.
	 * @param outageToRemove id property is used for comparison with existing outages in the collection.
	 * @return true if an object was actually removed
	 */
	public synchronized boolean remove(PlannedOutage outageToRemove) {
		return outages.remove(outageToRemove);
	}
}
