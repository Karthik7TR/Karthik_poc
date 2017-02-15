package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.outage.dao.OutageDao;
import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class OutageServiceImpl implements OutageService
{
    //private static Logger log = LogManager.getLogger(OutageServiceImpl.class);
    private OutageDao dao;

    /**
     * Returns all Outage entities that are scheduled for current and future.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlannedOutage> getAllActiveAndScheduledPlannedOutages()
    {
        return dao.getAllActiveAndScheduledPlannedOutages();
    }

    /**
     * Returns all Outage entities including past outages.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlannedOutage> getAllPlannedOutages()
    {
        return dao.getAllPlannedOutages();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlannedOutage> getAllPlannedOutagesForType(final Long outageTypeId)
    {
        return dao.getAllPlannedOutagesForType(outageTypeId);
    }

    /**
     * Returns all Outage entities that are scheduled and displayed to the user
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlannedOutage> getAllPlannedOutagesToDisplay()
    {
        final Date midnight = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        final Date endDate = DateUtils.addDays(midnight, PlannedOutage.NUMBER_DAYS_DISPLAY);

        return dao.getAllPlannedOutagesToDisplay(endDate);
    }

    /**
     * Get the Outage entity with the give id.
     */
    @Override
    @Transactional(readOnly = true)
    public PlannedOutage findPlannedOutageByPrimaryKey(final Long id)
    {
        return dao.findPlannedOutageByPrimaryKey(id);
    }

    /**
     * Save the Outage entity in the database.
     * Used for update and create.
     */
    @Override
    @Transactional
    public void savePlannedOutage(final PlannedOutage outage)
    {
        dao.savePlannedOutage(outage);
    }

    /**
     * Delete the Outage entity in the database.
     */
    @Override
    @Transactional
    public void deletePlannedOutage(final Long id)
    {
        dao.deletePlannedOutage(findPlannedOutageByPrimaryKey(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutageType> getAllOutageType()
    {
        return dao.getAllOutageType();
    }

    @Override
    @Transactional(readOnly = true)
    public OutageType findOutageTypeByPrimaryKey(final Long id)
    {
        return dao.findOutageTypeByPrimaryKey(id);
    }

    @Override
    @Transactional
    public void saveOutageType(final OutageType outageType)
    {
        if (outageType.getId() != null)
        {
            final OutageType persistantType = findOutageTypeByPrimaryKey(outageType.getId());
            persistantType.setSubSystem(outageType.getSubSystem());
            persistantType.setSystem(outageType.getSystem());
            dao.saveOutageType(persistantType);
        }
        else
        {
            dao.saveOutageType(outageType);
        }
    }

    @Override
    @Transactional
    public void deleteOutageType(final Long id)
    {
        dao.deleteOutageType(findOutageTypeByPrimaryKey(id));
    }

    @Required
    public void setOutageDao(final OutageDao dao)
    {
        this.dao = dao;
    }
}
