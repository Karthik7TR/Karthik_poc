package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;

/**
 * A set of processing methods around a container of PlannedOutages.
 * This is the in-memory set of outages that are kept in sync with the database state
 * and are loaded at started and subsequently updated and then pushed via the manager admin page.
 */
public interface OutageProcessor
{
    /**
     * Determine if we are in the middle of a planned outage and
     * send notification email if we are just starting or ending an outage.
     * @return the current outage event if we are in one, otherwise null.
     */
    PlannedOutage processPlannedOutages();

    PlannedOutage findPlannedOutageInContainer(Date timeInstant);

    PlannedOutage findExpiredOutageInContainer(Date expiredTime);

    void addPlannedOutageToContainer(PlannedOutage outage);

    boolean deletePlannedOutageFromContainer(PlannedOutage outage);
}
