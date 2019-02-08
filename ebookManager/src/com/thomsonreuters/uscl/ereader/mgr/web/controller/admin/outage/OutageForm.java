package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import static com.thomsonreuters.uscl.ereader.mgr.web.FormUtils.parseDate;

import java.util.Date;

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

    public void initialize(final PlannedOutage outage) {
        plannedOutageId = outage.getId();
        outageTypeId = outage.getOutageType().getId();
        startTimeString = parseDate(outage.getStartTime());
        endTimeString = parseDate(outage.getEndTime());
        reason = outage.getReason();
        systemImpactDescription = outage.getSystemImpactDescription();
        serversImpacted = outage.getServersImpacted();
    }

    public PlannedOutage createRawPlannedOutage() {
        final PlannedOutage outage = new PlannedOutage();
        outage.setId(plannedOutageId);
        return outage;
    }

    public PlannedOutage createPlannedOutage() {
        final PlannedOutage outage = createRawPlannedOutage();

        final OutageType type = new OutageType();
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

    public void setPlannedOutageId(final Long id) {
        plannedOutageId = id;
    }

    public Long getOutageTypeId() {
        return outageTypeId;
    }

    public void setOutageTypeId(final Long outageTypeId) {
        this.outageTypeId = outageTypeId;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public void setStartTimeString(final String startTimeString) {
        this.startTimeString = startTimeString;
    }

    public String getEndTimeString() {
        return endTimeString;
    }

    public void setEndTimeString(final String endTimeString) {
        this.endTimeString = endTimeString;
    }

    public Date getStartTime() {
        return parseDate(startTimeString);
    }

    public void setStartTime(final Date startTime) {
        startTimeString = parseDate(startTime);
    }

    public Date getEndTime() {
        return parseDate(endTimeString);
    }

    public void setEndTime(final Date endTime) {
        endTimeString = parseDate(endTime);
    }

    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    public String getSystemImpactDescription() {
        return systemImpactDescription;
    }

    public void setSystemImpactDescription(final String systemImpactDescription) {
        this.systemImpactDescription = systemImpactDescription;
    }

    public String getServersImpacted() {
        return serversImpacted;
    }

    public void setServersImpacted(final String serversImpacted) {
        this.serversImpacted = serversImpacted;
    }
}
