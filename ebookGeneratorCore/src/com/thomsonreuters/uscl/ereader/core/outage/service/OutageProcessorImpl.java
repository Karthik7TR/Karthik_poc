/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutageContainer;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

public class OutageProcessorImpl implements OutageProcessor {
	private static Logger log = LogManager.getLogger(OutageProcessorImpl.class);

	private OutageService outageService;
	private CoreService coreService;
	private UserPreferenceService userPreferenceService;
	private PlannedOutageContainer plannedOutageContainer = new PlannedOutageContainer();

	/**
	 * 
	 * @return the outage object if we are currently in the middle of an outage.
	 */
	@Override
	@Transactional
	public synchronized PlannedOutage processPlannedOutages() {
		Date timeNow = new Date();
		PlannedOutage outage = plannedOutageContainer.findOutage(timeNow);
		Collection<InternetAddress> recipients = null;
		if (outage != null) {
			recipients = getOutageEmailRecipients();
			// If not already sent, send email indicating an outage has started
			if (!outage.isNotificationEmailSent()) {
				outage.setNotificationEmailSent(true);
				outageService.savePlannedOutage(outage);
				String subject = String.format("Start of eBook generator outage on host %s", getHostName());
				sendOutageEmail(recipients, subject, outage);
			}
		}

		// Check for any outages that have now passed, and remove them from the collection
		PlannedOutage expiredOutage = plannedOutageContainer.findExpiredOutage(timeNow);
		if (expiredOutage != null) {
			// Find the recipients we have not already
			recipients = (recipients == null) ? getOutageEmailRecipients() : recipients;
			// If not already sent, send email indicating that the outage is over
			if (!expiredOutage.isAllClearEmailSent()) {
				expiredOutage.setAllClearEmailSent(true);
				outageService.savePlannedOutage(expiredOutage);
				String subject = String.format("End of eBook generator outage on host %s", getHostName());
				sendOutageEmail(recipients, subject, expiredOutage);
			}
			// Removed the expired outage now that it is complete
			plannedOutageContainer.remove(expiredOutage);
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
			sendOutageEmail(getOutageEmailRecipients(), subject, outage);
		}
		return plannedOutageContainer.remove(outage);
	}
	
	/**
	 * Create the list of email recipients for outage notifications.
	 * This is comprised of a static list as specified by a spring property, and a dynamic list which
	 * comes from the unique set of email addresses from the user preference table.
	 */
	public Collection<InternetAddress> getOutageEmailRecipients() {
		Set<InternetAddress> uniqueRecipients = userPreferenceService.findAllUniqueEmailAddresses();
		return coreService.createEmailRecipients(uniqueRecipients);
	}
	
	private void sendOutageEmail(Collection<InternetAddress> recipients, String subject, PlannedOutage outage) {
		if (recipients.size() > 0) {
			// Make the subject line also be the first line of the body, because it is easier to read in Outlook preview pane
			String body = subject + "\n\n";
			body += outage.toEmailBody();
			log.debug("Sending outage notification email to: " + recipients);			
			EmailNotification.send(recipients, subject, body);
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
	public void setOutageService(OutageService service) {
		this.outageService = service;
	}
	@Required
	public void setUserPreferenceService(UserPreferenceService service) {
		this.userPreferenceService = service;
	}
	@Required
	public void setCoreService(CoreService service) {
		this.coreService = service;
	}
}
