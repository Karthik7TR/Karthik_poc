package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutageContainer;

public interface OutageService {
	
	/**
	 * Returns all Outage entities that are scheduled for current and future.
	 */
	public List<PlannedOutage> getAllActiveAndScheduledPlannedOutages();
	
	/**
	 * Returns all Outage entities including past outages.
	 */
	public List<PlannedOutage> getAllPlannedOutages();
	
	/**
	 * Get the Outage entity with the give id.
	 */
	public PlannedOutage findPlannedOutageByPrimaryKey(Long id);
	
	/**
	 * Save the Outage entity in the database.
	 * Used for update and create.
	 */
	public void savePlannedOutage(PlannedOutage outage);
	
	/**
	 * Delete the Outage entity in the database.
	 */
	public void deletePlannedOutage(Long id);
	
	public List<OutageType> getAllOutageType();
	
	public OutageType findOutageTypeByPrimaryKey(Long id);
	
	public void saveOutageType(OutageType outageType);
	
	public void deleteOutageType(Long id);
	
	public PlannedOutageContainer getPlannedOutageContainer();
	
	/**
	 * Determine if we are in the middle of a planned outage and 
	 * send notification email if we are just starting or ending an outage.
	 * @return the current outage event if we are in one, otherwise null.
	 */
	public PlannedOutage processPlannedOutages();

}
