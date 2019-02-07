package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.outage.dao.OutageDao;
import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("outageService")
public class OutageServiceImpl implements OutageService {
    private final OutageDao dao;

    @Autowired
    public OutageServiceImpl(final OutageDao dao) {
        this.dao = dao;
    }

    /**
     * Returns all Outage entities that are scheduled for current and future.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlannedOutage> getAllActiveAndScheduledPlannedOutages() {
        return dao.getAllActiveAndScheduledPlannedOutages();
    }

    /**
     * Returns all Outage entities including past outages.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlannedOutage> getAllPlannedOutages() {
        return dao.getAllPlannedOutages();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlannedOutage> getActiveAndScheduledPlannedOutagesForType(final Long outageTypeId) {
        return dao.getActiveAndScheduledPlannedOutagesForType(outageTypeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlannedOutage> getInactivePlannedOutagesForType(final Long outageTypeId) {
        return dao.getInactivePlannedOutagesForType(outageTypeId);
    }

    /**
     * Returns all Outage entities that are scheduled and displayed to the user
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlannedOutage> getAllPlannedOutagesToDisplay() {
        final Date midnight = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        final Date endDate = DateUtils.addDays(midnight, PlannedOutage.NUMBER_DAYS_DISPLAY);

        return dao.getAllPlannedOutagesToDisplay(endDate);
    }

    /**
     * Get the Outage entity with the give id.
     */
    @Override
    @Transactional(readOnly = true)
    public PlannedOutage findPlannedOutageByPrimaryKey(final Long id) {
        return dao.findPlannedOutageByPrimaryKey(id);
    }

    @Transactional(readOnly = true)
    private List<PlannedOutage> getPlannedOutagesForType(final OutageType outageType) {
        return dao.getPlannedOutagesForType(outageType);
    }

    /**
     * Save the Outage entity in the database.
     * Used for update and create.
     */
    @Override
    @Transactional
    public void savePlannedOutage(final PlannedOutage outage) {
        dao.savePlannedOutage(outage);
    }

    /**
     * Delete the Outage entity in the database.
     */
    @Override
    @Transactional
    public void deletePlannedOutage(final Long id) {
        dao.deletePlannedOutage(findPlannedOutageByPrimaryKey(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutageType> getAllActiveOutageTypes() {
        return dao.getAllActiveOutageTypes();
    }

    @Override
    @Transactional(readOnly = true)
    public OutageType findOutageTypeByPrimaryKey(final Long id) {
        return dao.findOutageTypeByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = true)
    public OutageType findOutageTypeBySystemAndSubSystem(final String system, final String subSystem) {
        return dao.findOutageTypeBySystemAndSubSystem(system, subSystem);
    }

    @Override
    @Transactional
    public void saveOutageType(final OutageType outageTypeToSave) {
        final OutageType outageTypeByName = findOutageTypeBySystemAndSubSystem(outageTypeToSave.getSystem(), outageTypeToSave.getSubSystem());

        if (outageTypeToSave.getId() != null) {
            final OutageType outageTypeById = findOutageTypeByPrimaryKey(outageTypeToSave.getId());

            if (outageTypeByName != null && outageTypeByName.getId() != outageTypeById.getId()) {
                //merge two objects because they has the same name
                final List<PlannedOutage> outagesWithAnotherType = getPlannedOutagesForType(outageTypeByName);
                outagesWithAnotherType.forEach(outage -> outage.setOutageType(outageTypeById));
                dao.savePlannedOutages(outagesWithAnotherType);
                dao.deleteOutageType(outageTypeByName);
            }

            outageTypeById.setSubSystem(outageTypeToSave.getSubSystem());
            outageTypeById.setSystem(outageTypeToSave.getSystem());
            dao.saveOutageType(outageTypeById);
        } else {
            if (outageTypeByName != null) {
                //get OutageType back from removed state
                outageTypeByName.setRemoved(false);
                dao.saveOutageType(outageTypeByName);
            } else {
                //create new OutageType
                dao.saveOutageType(outageTypeToSave);
            }
        }
    }

    @Override
    @Transactional
    public void removeOutageType(final Long id) {
        dao.removeOutageType(findOutageTypeByPrimaryKey(id));
    }

    @Override
    @Transactional
    public void deleteOutageType(final Long id) {
        dao.deleteOutageType(findOutageTypeByPrimaryKey(id));
    }
}
