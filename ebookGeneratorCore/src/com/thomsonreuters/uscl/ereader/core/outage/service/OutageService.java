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
     * Returns all Outage entities including for outage type.
     */
    List<PlannedOutage> getAllPlannedOutagesForType(Long outageTypeId);

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

    List<OutageType> getAllOutageType();

    OutageType findOutageTypeByPrimaryKey(Long id);

    void saveOutageType(OutageType outageType);

    void deleteOutageType(Long id);
}
