package com.thomsonreuters.uscl.ereader.orchestrate.engine.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;

/**
 * Container and convenience methods for all the planned outage events.
 */
public class PlannedOutageContainer {
	//private static final Logger log = Logger.getLogger(PlannedOutageContainer.class);

	private Collection<PlannedOutage> outages = new ArrayList<PlannedOutage>();
	
	public PlannedOutageContainer() {
		super();
	}
	
	/**
	 * Add a new outage to the collection, removing any existing outage 
	 * object that already has the same key.
	 * @param outageToSave the new outage.
	 */
	public synchronized void save(PlannedOutage outageToSave) {
		remove(outageToSave);
		outages.add(outageToSave);
	}

	public synchronized void saveAll(Collection<PlannedOutage> allOutages) {
		for (PlannedOutage outage : allOutages) {
			save(outage);
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
			if ((timeInstant.equals(outage.getStartTime()) || timeInstant.after(outage.getStartTime()))
				 && timeInstant.before(outage.getEndTime())) {
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
