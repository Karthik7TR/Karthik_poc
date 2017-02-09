package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

public class OutageForm
{
    //private static final Logger log = LogManager.getLogger(OutageForm.class);
    public static final String FORM_NAME = "outageForm";

    private Long plannedOutageId;
    private Long outageTypeId;
    private String startTimeString;
    private String endTimeString;
    private String reason;
    private String systemImpactDescription;
    private String serversImpacted;

    public OutageForm()
    {
        super();
    }

    public void initialize(final PlannedOutage outage)
    {
        plannedOutageId = outage.getId();
        outageTypeId = outage.getOutageType().getId();
        startTimeString = parseDate(outage.getStartTime());
        endTimeString = parseDate(outage.getEndTime());
        reason = outage.getReason();
        systemImpactDescription = outage.getSystemImpactDescription();
        serversImpacted = outage.getServersImpacted();
    }

    public PlannedOutage createPlannedOutage()
    {
        final PlannedOutage outage = new PlannedOutage();
        outage.setId(plannedOutageId);

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

    public Long getPlannedOutageId()
    {
        return plannedOutageId;
    }

    public void setPlannedOutageId(final Long id)
    {
        plannedOutageId = id;
    }

    public Long getOutageTypeId()
    {
        return outageTypeId;
    }

    public void setOutageTypeId(final Long outageTypeId)
    {
        this.outageTypeId = outageTypeId;
    }

    public String getStartTimeString()
    {
        return startTimeString;
    }

    public void setStartTimeString(final String startTimeString)
    {
        this.startTimeString = startTimeString;
    }

    public String getEndTimeString()
    {
        return endTimeString;
    }

    public void setEndTimeString(final String endTimeString)
    {
        this.endTimeString = endTimeString;
    }

    public Date getStartTime()
    {
        return parseDate(startTimeString);
    }

    public void setStartTime(final Date startTime)
    {
        startTimeString = parseDate(startTime);
    }

    public Date getEndTime()
    {
        return parseDate(endTimeString);
    }

    public void setEndTime(final Date endTime)
    {
        endTimeString = parseDate(endTime);
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(final String reason)
    {
        this.reason = reason;
    }

    public String getSystemImpactDescription()
    {
        return systemImpactDescription;
    }

    public void setSystemImpactDescription(final String systemImpactDescription)
    {
        this.systemImpactDescription = systemImpactDescription;
    }

    public String getServersImpacted()
    {
        return serversImpacted;
    }

    public void setServersImpacted(final String serversImpacted)
    {
        this.serversImpacted = serversImpacted;
    }

    public static String parseDate(final Date date)
    {
        if (date != null)
        {
            final SimpleDateFormat sdf = new SimpleDateFormat(CoreConstants.DATE_TIME_FORMAT_PATTERN);
            return sdf.format(date);
        }
        return null;
    }

    public static Date parseDate(final String dateString)
    {
        Date date = null;
        try
        {
            if (StringUtils.isNotBlank(dateString))
            {
                final String[] parsePatterns = {CoreConstants.DATE_TIME_FORMAT_PATTERN};
                date = DateUtils.parseDate(dateString, parsePatterns);
            }
        }
        catch (final ParseException e)
        {
            // Intentionally left blank
        }
        return date;
    }
}
