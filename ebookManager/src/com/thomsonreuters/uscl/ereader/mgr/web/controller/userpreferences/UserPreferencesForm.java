package com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.AutoPopulatingList;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;

public class UserPreferencesForm {
	
	public static final String FORM_NAME = "preferencesForm";

	public enum HomepageProperty {
		LIBRARY, PROVIEW_LIST, AUDIT, JOBS, QUEUED, ADMINISTRATION
	}
	
	private HomepageProperty startPage;
	private String auditFilterProviewName;
	private String auditFilterTitleId;
	private String libraryFilterProviewName;
	private String libraryFilterTitleId;
	private String jobSummaryFilterProviewName;
	private String jobSummaryFilterTitleId;
	private List<String> emails;

	public UserPreferencesForm() {
		super();
		this.startPage = HomepageProperty.LIBRARY;

		emails = new AutoPopulatingList<String>(String.class);
	}
	
	public void load(UserPreference preference) {
		if(preference != null) {
			String startPageStr = preference.getStartPage();
			
			if(StringUtils.isNotBlank(startPageStr)) {
				startPage = HomepageProperty.valueOf(startPageStr);
			} else {
				startPage = HomepageProperty.LIBRARY;
			}
			
			auditFilterProviewName = preference.getAuditProviewName();
			auditFilterTitleId = preference.getAuditTitleId();
			libraryFilterProviewName = preference.getLibraryProviewName();
			libraryFilterTitleId = preference.getLibraryTitleId();
			jobSummaryFilterProviewName = preference.getJobSummaryProviewName();
			jobSummaryFilterTitleId = preference.getJobSummaryTitleId();
			emails = preference.getEmailAddressList();
		}
	}
	
	public UserPreference makeUserPreference() {
		UserPreference preference = new UserPreference();
		preference.setAuditProviewName(auditFilterProviewName);
		preference.setAuditTitleId(auditFilterTitleId);
		preference.setJobSummaryProviewName(jobSummaryFilterProviewName);
		preference.setJobSummaryTitleId(jobSummaryFilterTitleId);
		preference.setLibraryProviewName(libraryFilterProviewName);
		preference.setLibraryTitleId(libraryFilterTitleId);
		preference.setStartPage(startPage.toString());
		
		String emailStr = StringUtils.join(emails, ",");
		preference.setEmails(emailStr);
		
		return preference;
	}

	public HomepageProperty getStartPage() {
		return startPage;
	}
	
	public void setStartPage(HomepageProperty startPage) {
		this.startPage = startPage;
	}

	public String getAuditFilterProviewName() {
		return auditFilterProviewName;
	}

	public void setAuditFilterProviewName(String auditFilterProviewName) {
		this.auditFilterProviewName = auditFilterProviewName;
	}

	public String getAuditFilterTitleId() {
		return auditFilterTitleId;
	}

	public void setAuditFilterTitleId(String auditFilterTitleId) {
		this.auditFilterTitleId = auditFilterTitleId;
	}

	public String getLibraryFilterProviewName() {
		return libraryFilterProviewName;
	}

	public void setLibraryFilterProviewName(String libraryFilterProviewName) {
		this.libraryFilterProviewName = libraryFilterProviewName;
	}

	public String getLibraryFilterTitleId() {
		return libraryFilterTitleId;
	}

	public void setLibraryFilterTitleId(String libraryFilterTitleId) {
		this.libraryFilterTitleId = libraryFilterTitleId;
	}

	public String getJobSummaryFilterProviewName() {
		return jobSummaryFilterProviewName;
	}

	public void setJobSummaryFilterProviewName(String jobSummaryFilterProviewName) {
		this.jobSummaryFilterProviewName = jobSummaryFilterProviewName;
	}

	public String getJobSummaryFilterTitleId() {
		return jobSummaryFilterTitleId;
	}

	public void setJobSummaryFilterTitleId(String jobSummaryFilterTitleId) {
		this.jobSummaryFilterTitleId = jobSummaryFilterTitleId;
	}

	public List<String> getEmails() {
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	public String getURL() {
		String url = "";
		
		switch(startPage) {
			case PROVIEW_LIST:
				url = WebConstants.MVC_PROVIEW_TITLES;
				break;
			case AUDIT:
				url = WebConstants.MVC_BOOK_AUDIT_LIST;
				break;
			case JOBS:
				url = WebConstants.MVC_JOB_SUMMARY;
				break;
			case QUEUED:
				url = WebConstants.MVC_JOB_QUEUE;
				break;
			case ADMINISTRATION:
				url = WebConstants.MVC_ADMIN_MAIN;
				break;
			default:
				url = WebConstants.MVC_BOOK_LIBRARY_LIST;
				break;
		}
		
		return url;
	}
}
