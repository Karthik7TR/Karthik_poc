/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;

public class ProviewTitleForm {
	public static final String FORM_NAME = "proviewTitleInfoForm";

	public enum Command {
		REMOVE, DELETE, PROMOTE, REFRESH, PAGESIZE
	};

	private String titleId;
	private String version;
	private String status;
	private String lastUpdate;
	private String comments;
	private Command command;
	private String objectsPerPage;

	public ProviewTitleForm() {
		super();
	}

	public ProviewAudit createAudit() {
		ProviewAudit audit = new ProviewAudit();
		audit.setAuditNote(comments);
		audit.setBookLastUpdated(parseDate(lastUpdate));
		audit.setBookVersion(version);
		audit.setProviewRequest(command.toString());
		audit.setRequestDate(new Date());
		audit.setTitleId(titleId);
		audit.setUsername(UserUtils.getAuthenticatedUserName());

		return audit;
	}

	public ProviewTitleForm(String titleId, String version, String status,
			String lastUpdate) {
		super();
		this.titleId = titleId;
		this.version = version;
		this.status = status;
		this.lastUpdate = lastUpdate;
	}

	public String getObjectsPerPage() {
		return objectsPerPage;
	}

	public void setObjectsPerPage(String objectsPerPage) {
		this.objectsPerPage = objectsPerPage;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command cmd) {
		this.command = cmd;
	}

	private Date parseDate(String dateString) {
		Date date = null;
		try {
			if (StringUtils.isNotBlank(dateString)) {
				String[] parsePatterns = { "yyyyMMdd" };
				date = DateUtils.parseDate(dateString, parsePatterns);
			}
		} catch (ParseException e) {
			date = null;
		}
		return date;
	}
}
