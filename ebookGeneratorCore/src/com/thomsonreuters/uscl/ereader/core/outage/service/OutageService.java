package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;

public interface OutageService {
    /**
     * Returns all Outage entities that are scheduled for current and future.
     */
    List<PlannedOutage> getAllActiveAndScheduledPlannedOutages();

    /**
     * Returns all Outage entities including past outages.
     */
    List<PlannedOutage> getAllPlannedOutages();

    /**
     * Returns Outage entities with specified outage type and endTime in the future
     */
    List<PlannedOutage> getActiveAndScheduledPlannedOutagesForType(Long outageTypeId);

    /**
     * Returns Outage entities with specified outage type which and endTime in the past
     */
    List<PlannedOutage> getInactivePlannedOutagesForType(Long outageTypeId);

    /**
     * Returns all Outage entities that are scheduled and displayed to the user
     */
    List<PlannedOutage> getAllPlannedOutagesToDisplay();

    /**
     * Get the Outage entity with the give id.
     */
    PlannedOutage findPlannedOutageByPrimaryKey(Long id);

    /**
     * Save the Outage entity in the database.
     * Used for update and create.
     */
    void savePlannedOutage(PlannedOutage outage);

    /**
     * Delete the Outage entity in the database.
     */
    void deletePlannedOutage(Long id);

	/**
	 * Get outage types which are not marked as 'removed'
	 */
    List<OutageType> getAllActiveOutageTypes();

    OutageType findOutageTypeByPrimaryKey(Long id);

    OutageType findOutageTypeBySystemAndSubSystem(String system, String subSystem);

    void saveOutageType(OutageType outageType);

    void removeOutageType(Long id);

    void deleteOutageType(Long id);
}
