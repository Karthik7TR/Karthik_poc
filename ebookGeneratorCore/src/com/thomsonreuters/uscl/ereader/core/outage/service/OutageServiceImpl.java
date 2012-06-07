package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.outage.dao.OutageDao;
import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;

public class OutageServiceImpl implements OutageService {
	//private static Logger log = Logger.getLogger(OutageServiceImpl.class);
	private OutageDao dao;
	
	/**
	 * Returns all Outage entities that are scheduled for current and future.
	 */
	@Transactional(readOnly=true)
	public List<PlannedOutage> getAllActiveAndScheduledPlannedOutages() {
		return dao.getAllActiveAndScheduledPlannedOutages();
	}
	
	/**
	 * Returns all Outage entities including past outages.
	 */
	@Transactional(readOnly=true)
	public List<PlannedOutage> getAllPlannedOutages() {
		return dao.getAllPlannedOutages();
	}
	
	/**
	 * Returns all Outage entities that are scheduled and displayed to the user
	 */
	@Transactional(readOnly=true)
	public List<PlannedOutage> getAllPlannedOutagesToDisplay() {
		Date midnight = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		Date endDate = DateUtils.addDays(midnight, PlannedOutage.NUMBER_DAYS_DISPLAY);
		
		return dao.getAllPlannedOutagesToDisplay(endDate);
	}
	
	/**
	 * Get the Outage entity with the give id.
	 */
	@Transactional(readOnly=true)
	public PlannedOutage findPlannedOutageByPrimaryKey(Long id) {
		return dao.findPlannedOutageByPrimaryKey(id);
	}
	
	/**
	 * Save the Outage entity in the database.
	 * Used for update and create.
	 */
	@Transactional
	public void savePlannedOutage(PlannedOutage outage) {
		dao.savePlannedOutage(outage);
	}
	
	/**
	 * Delete the Outage entity in the database.
	 */
	@Transactional
	public void deletePlannedOutage(Long id) {
		dao.deletePlannedOutage(findPlannedOutageByPrimaryKey(id));
	}
	
	@Transactional(readOnly=true)
	public List<OutageType> getAllOutageType() {
		return dao.getAllOutageType();
	}
	
	@Transactional(readOnly=true)
	public OutageType findOutageTypeByPrimaryKey(Long id) {
		return dao.findOutageTypeByPrimaryKey(id);
	}
	
	@Transactional
	public void saveOutageType(OutageType outageType) {
		
		if(outageType.getId() != null) {
			OutageType persistantType = findOutageTypeByPrimaryKey(outageType.getId());
			persistantType.setSubSystem(outageType.getSubSystem());
			persistantType.setSystem(outageType.getSystem());
			dao.saveOutageType(persistantType);
		} else {
			dao.saveOutageType(outageType);
		}
	}
	
	@Transactional
	public void deleteOutageType(Long id) {
		dao.deleteOutageType(findOutageTypeByPrimaryKey(id));
	}
	
	@Required
	public void setOutageDao(OutageDao dao) {
		this.dao = dao;
	}
}
