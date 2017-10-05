package com.thomsonreuters.uscl.ereader.core.outage.dao;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;

public interface OutageDao {
    /**
     * Returns all Outage entities that are scheduled for current and future.
     */
    List<PlannedOutage> getAllActiveAndScheduledPlannedOutages();

    /**
     * Returns all Outage entities including past outages.
     */
    List<PlannedOutage> getAllPlannedOutages();

    List<PlannedOutage> getAllPlannedOutagesForType(Long outageTypeId);

    /**
     * Returns all Outage entities that are scheduled and displayed to the user
     * @param endDate - filter used to limit PlannedOutages
     */
    List<PlannedOutage> getAllPlannedOutagesToDisplay(Date endDate);

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
    void deletePlannedOutage(PlannedOutage outage);

    List<OutageType> getAllOutageType();

    OutageType findOutageTypeByPrimaryKey(Long id);

    void saveOutageType(OutageType outageType);

    void deleteOutageType(OutageType outageType);
}
