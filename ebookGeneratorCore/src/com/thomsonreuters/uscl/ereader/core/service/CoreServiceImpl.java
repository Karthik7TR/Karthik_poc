package com.thomsonreuters.uscl.ereader.core.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;

public class CoreServiceImpl implements CoreService {
	
	private UserPreferenceService userPreferenceService;
	private InternetAddress groupEmailAddress;
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection<InternetAddress> getEmailRecipientsByUsername(String username) {
		List<InternetAddress> userRecipientInternetAddressList = Collections.EMPTY_LIST;
		UserPreference userPreference = userPreferenceService.findByUsername(username);
		if (userPreference != null) {
			String userRecipientCsv = userPreference.getEmails();
			List<String> userRecipientStringList = UserPreference.toStringEmailAddressList(userRecipientCsv);
			userRecipientInternetAddressList = UserPreference.toInternetAddressList(userRecipientStringList);
		}
		return createEmailRecipients(userRecipientInternetAddressList);
	}
	
	@Override
	public Collection<InternetAddress> createEmailRecipients(Collection<InternetAddress> userRecipientInternetAddressList) {
		Set<InternetAddress> uniqueRecipients = new HashSet<InternetAddress>();
		uniqueRecipients.addAll(userRecipientInternetAddressList);
		uniqueRecipients.add(groupEmailAddress);
		return uniqueRecipients;
	}
	
	@Required
	public void setGroupEmailAddress(InternetAddress addr) {
		this.groupEmailAddress = addr;
	}
	
	@Required
	public void setUserPreferenceService(UserPreferenceService userPreferenceService) {
		this.userPreferenceService = userPreferenceService;
	}
}
