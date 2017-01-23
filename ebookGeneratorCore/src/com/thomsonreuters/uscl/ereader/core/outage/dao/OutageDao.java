package com.thomsonreuters.uscl.ereader.core.outage.dao;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;

public interface OutageDao {
	
	/**
	 * Returns all Outage entities that are scheduled for current and future.
	 */
	public List<PlannedOutage> getAllActiveAndScheduledPlannedOutages();
	
	/**
	 * Returns all Outage entities including past outages.
	 */
	public List<PlannedOutage> getAllPlannedOutages();
	
	
	public List<PlannedOutage> getAllPlannedOutagesForType(Long outageTypeId);
	
	/**
	 * Returns all Outage entities that are scheduled and displayed to the user
	 * @param endDate - filter used to limit PlannedOutages
	 */
	public List<PlannedOutage> getAllPlannedOutagesToDisplay(Date endDate);
		
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
	public void deletePlannedOutage(PlannedOutage outage);
	
	public List<OutageType> getAllOutageType();
	
	public OutageType findOutageTypeByPrimaryKey(Long id);
	
	public void saveOutageType(OutageType outageType);
	
	public void deleteOutageType(OutageType outageType);

}
