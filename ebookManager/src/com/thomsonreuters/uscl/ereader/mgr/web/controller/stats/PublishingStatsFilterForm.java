package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateUtils;

/**
 * The form backing object that holds the data the user enters into the Publishing Stats filter HTML form.
 */
public class PublishingStatsFilterForm
{
    public static final String FORM_NAME = "publishingStatsFilterForm";

    public enum FilterCommand
    {
        SEARCH,
        RESET
    };

    //private static final Logger log = LogManager.getLogger(PublishingStatsFilterForm.class);

    private String titleId;
    private String proviewDisplayName;
    private String fromDateString;
    private String toDateString;
    private Long bookDefinitionId;
    private FilterCommand command;

    public PublishingStatsFilterForm()
    {
        initialize();
    }

    public PublishingStatsFilterForm(final Long bookDefinitionId)
    {
        populate(null, null, null, null, bookDefinitionId);
    }

    /**
     * Set all values back to defaults.
     * Used in resetting the form.
     */
    public void initialize()
    {
        populate(null, null, null, null, null);
    }

    public void populate(
        final String titleId,
        final String proviewDisplayName,
        final String fromDateString,
        final String toDateString,
        final Long bookId)
    {
        this.titleId = titleId;
        this.proviewDisplayName = proviewDisplayName;
        this.fromDateString = fromDateString;
        this.toDateString = toDateString;
        bookDefinitionId = bookId;
    }

    public String getProviewDisplayName()
    {
        return proviewDisplayName;
    }

    public FilterCommand getFilterCommand()
    {
        return command;
    }

    public String getTitleId()
    {
        return titleId;
    }

    public Date getFromDate()
    {
        return parseDate(fromDateString);
    }

    public String getFromDateString()
    {
        return fromDateString;
    }

    public Date getToDate()
    {
        return parseDate(toDateString);
    }

    public String getToDateString()
    {
        return toDateString;
    }

    public Long getBookDefinitionId()
    {
        return bookDefinitionId;
    }

    public void setProviewDisplayName(final String name)
    {
        proviewDisplayName = (name != null) ? name.trim() : null;
    }

    public void setFilterCommand(final FilterCommand cmd)
    {
        command = cmd;
    }

    public void setTitleId(final String titleId)
    {
        this.titleId = (titleId != null) ? titleId.trim() : null;
    }

    public void setFromDate(final Date fromDate)
    {
        fromDateString = parseDate(fromDate);
    }

    public void setFromDateString(final String fromDate)
    {
        fromDateString = fromDate;
    }

    public void setToDateString(final String toDate)
    {
        toDateString = toDate;
    }

    public void setToDate(final Date toDate)
    {
        toDateString = parseDate(toDate);
    }

    public void setBookDefinitionId(final Long bookDefinitionId)
    {
        this.bookDefinitionId = bookDefinitionId;
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
            //Intentionally left blank
        }
        return date;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
