/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;

public class OutageForm {
	//private static final Logger log = LogManager.getLogger(OutageForm.class);
	public static final String FORM_NAME = "outageForm";
	
	private Long plannedOutageId;
	private Long outageTypeId;
	private String startTimeString;
	private String endTimeString;
	private String reason;
	private String systemImpactDescription;
	private String serversImpacted;
	
	public OutageForm() {
		super();
	}
	
	public void initialize(PlannedOutage outage) {
		this.plannedOutageId = outage.getId();
		this.outageTypeId = outage.getOutageType().getId();
		this.startTimeString = parseDate(outage.getStartTime());
		this.endTimeString = parseDate(outage.getEndTime());
		this.reason = outage.getReason();
		this.systemImpactDescription = outage.getSystemImpactDescription();
		this.serversImpacted = outage.getServersImpacted();
	}
	
	public PlannedOutage createPlannedOutage() {
		PlannedOutage outage = new PlannedOutage();
		outage.setId(plannedOutageId);
		
		OutageType type = new OutageType();
		type.setId(outageTypeId);
		outage.setOutageType(type);
		outage.setStartTime(parseDate(startTimeString));
		outage.setEndTime(parseDate(endTimeString));
		outage.setReason(reason);
		outage.setSystemImpactDescription(systemImpactDescription);
		outage.setServersImpacted(serversImpacted);
		outage.setUpdatedBy(UserUtils.getAuthenticatedUserName());
		return outage;
	}

	public Long getPlannedOutageId() {
		return plannedOutageId;
	}

	public void setPlannedOutageId(Long id) {
		this.plannedOutageId = id;
	}

	public Long getOutageTypeId() {
		return outageTypeId;
	}

	public void setOutageTypeId(Long outageTypeId) {
		this.outageTypeId = outageTypeId;
	}

	public String getStartTimeString() {
		return startTimeString;
	}

	public void setStartTimeString(String startTimeString) {
		this.startTimeString = startTimeString;
	}

	public String getEndTimeString() {
		return endTimeString;
	}

	public void setEndTimeString(String endTimeString) {
		this.endTimeString = endTimeString;
	}

	public Date getStartTime() {
		return parseDate(startTimeString);
	}

	public void setStartTime(Date startTime) {
		this.startTimeString = parseDate(startTime);
	}

	public Date getEndTime() {
		return parseDate(endTimeString);
	}

	public void setEndTime(Date endTime) {
		this.endTimeString = parseDate(endTime);
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSystemImpactDescription() {
		return systemImpactDescription;
	}

	public void setSystemImpactDescription(String systemImpactDescription) {
		this.systemImpactDescription = systemImpactDescription;
	}

	public String getServersImpacted() {
		return serversImpacted;
	}

	public void setServersImpacted(String serversImpacted) {
		this.serversImpacted = serversImpacted;
	}
	
	public static String parseDate(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(
					CoreConstants.DATE_TIME_FORMAT_PATTERN);
			return sdf.format(date);
		}
		return null;
	}

	public static Date parseDate(String dateString) {
		Date date = null;
		try {
			if (StringUtils.isNotBlank(dateString)) {
				String[] parsePatterns = { CoreConstants.DATE_TIME_FORMAT_PATTERN };
				date = DateUtils.parseDate(dateString, parsePatterns);
			}
		} catch (ParseException e) {
			date = null;
		}
		return date;
	}
	
}
