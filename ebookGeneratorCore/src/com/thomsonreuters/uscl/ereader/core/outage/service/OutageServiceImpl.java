package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.outage.dao.OutageDao;
import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutageContainer;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

public class OutageServiceImpl implements OutageService {
	
	private OutageDao dao;
	private PlannedOutageContainer plannedOutageContainer;
	private String outageEmailRecipients;
	
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
		dao.saveOutageType(outageType);
	}
	
	@Transactional
	public void deleteOutageType(Long id) {
		dao.deleteOutageType(findOutageTypeByPrimaryKey(id));
	}
	
	/**
	 * 
	 * @return the outage object if we are currently in the middle of an outage.
	 */
	@Override
	@Transactional
	public synchronized PlannedOutage processPlannedOutages() {
		Date timeNow = new Date();
		PlannedOutage outage = plannedOutageContainer.findOutage(timeNow);
		if (outage != null) {
			// If not already sent, send email indicating an outage has started
			if (!outage.isNotificationEmailSent()) {
				outage.setNotificationEmailSent(true);
				this.savePlannedOutage(outage);
				String subject = String.format("Start of eBook generator outage on host %s", getHostName());
				sendOutageEmail(subject, outage);
			}
		}

		// Check for any outages that have now passed, and remove them from the collection
		PlannedOutage expiredOutage = plannedOutageContainer.findExpiredOutage(timeNow);
		if (expiredOutage != null) {
			// If not already sent, send email indicating that the outage is over
			if (!expiredOutage.isAllClearEmailSent()) {
				expiredOutage.setAllClearEmailSent(true);
				this.savePlannedOutage(expiredOutage);
				String subject = String.format("End of eBook generator outage on host %s", getHostName());
				sendOutageEmail(subject, expiredOutage);
			}
		}
		return outage;
	}
	
	@Override
	public PlannedOutage findPlannedOutageInContainer(Date timeInstant) {
		return plannedOutageContainer.findOutage(timeInstant);
	}
	@Override
	public PlannedOutage findExpiredOutageInContainer(Date timeInstant) {
		return plannedOutageContainer.findExpiredOutage(timeInstant);
	}
	@Override
	public void addPlannedOutageToContainer(PlannedOutage outage) {
		plannedOutageContainer.add(outage);
	}
	
	@Override
	public boolean deletePlannedOutageFromContainer(PlannedOutage outage) {
		// If the outage is active at the time the user deletes it, then send an email message stating that
		// the outage is now over because it was deleted.
		if (outage.isActive(new Date())) {
			String subject = String.format("Deleted (end of) eBook generator outage on host %s",
										   getHostName());
			sendOutageEmail(subject, outage);
		}
		return plannedOutageContainer.remove(outage);
	}
	
	private void sendOutageEmail(String subject, PlannedOutage outage) {
		if (StringUtils.isNotBlank(outageEmailRecipients)) {
			// Make the subject line also be the first line of the body, because it is easier to read in Outlook preview pane
			String body = subject + "\n\n";
			body += outage.toEmailBody();
			EmailNotification.send(outageEmailRecipients, subject, body);
		}
	}
	
	public static String getHostName() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			return localHost.getHostName();
		} catch (UnknownHostException e) {
			return "<unknown>";
		}
	}
	
	@Required
	public void setOutageDao(OutageDao dao) {
		this.dao = dao;
	}

	/**
	 * Collection of planned outages.
	 * Used only in the generator.
	 * @param container
	 */
	public void setPlannedOutageContainer(PlannedOutageContainer container) {
		this.plannedOutageContainer = container;
	}

	/**
	 * Assign the list of recipients to receive notification when a outage begins and ends.
	 * Used only in the generator.
	 * @param csvRecipients a comma-separated list of valid SMTP email addresses
	 */
	public void setOutageEmailRecipients(String csvRecipients) {
		this.outageEmailRecipients = csvRecipients;
	}
}
