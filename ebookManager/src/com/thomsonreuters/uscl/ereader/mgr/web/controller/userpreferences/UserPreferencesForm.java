package com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences;

import java.util.List;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.AutoPopulatingList;

public class UserPreferencesForm
{
    public static final String FORM_NAME = "preferencesForm";

    public enum HomepageProperty
    {
        LIBRARY,
        PROVIEW_LIST,
        AUDIT,
        JOBS,
        QUEUED,
        ADMINISTRATION,
        GROUP_LIST
    }

    private HomepageProperty startPage;
    private String auditFilterProviewName;
    private String auditFilterTitleId;
    private String libraryFilterProviewName;
    private String libraryFilterTitleId;
    private String jobSummaryFilterProviewName;
    private String jobSummaryFilterTitleId;
    private String groupFilterName;
    private String groupFilterId;
    private List<String> emails;

    public UserPreferencesForm()
    {
        super();
        startPage = HomepageProperty.LIBRARY;

        emails = new AutoPopulatingList<>(String.class);
    }

    public void load(final UserPreference preference)
    {
        if (preference != null)
        {
            final String startPageStr = preference.getStartPage();

            if (StringUtils.isNotBlank(startPageStr))
            {
                startPage = HomepageProperty.valueOf(startPageStr);
            }
            else
            {
                startPage = HomepageProperty.LIBRARY;
            }

            auditFilterProviewName = preference.getAuditProviewName();
            auditFilterTitleId = preference.getAuditTitleId();
            libraryFilterProviewName = preference.getLibraryProviewName();
            libraryFilterTitleId = preference.getLibraryTitleId();
            jobSummaryFilterProviewName = preference.getJobSummaryProviewName();
            jobSummaryFilterTitleId = preference.getJobSummaryTitleId();
            groupFilterName = preference.getGroupListGroupName();
            groupFilterId = preference.getGroupListGroupId();
            emails = preference.getEmailAddressList();
        }
    }

    public UserPreference makeUserPreference()
    {
        final UserPreference preference = new UserPreference();
        preference.setAuditProviewName(auditFilterProviewName);
        preference.setAuditTitleId(auditFilterTitleId);
        preference.setJobSummaryProviewName(jobSummaryFilterProviewName);
        preference.setJobSummaryTitleId(jobSummaryFilterTitleId);
        preference.setLibraryProviewName(libraryFilterProviewName);
        preference.setLibraryTitleId(libraryFilterTitleId);
        preference.setGroupListGroupName(groupFilterName);
        preference.setGroupListGroupId(groupFilterId);
        preference.setStartPage(startPage.toString());

        final String emailStr = StringUtils.join(emails, ",");
        preference.setEmails(emailStr);

        return preference;
    }

    public HomepageProperty getStartPage()
    {
        return startPage;
    }

    public void setStartPage(final HomepageProperty startPage)
    {
        this.startPage = startPage;
    }

    public String getAuditFilterProviewName()
    {
        return auditFilterProviewName;
    }

    public void setAuditFilterProviewName(final String auditFilterProviewName)
    {
        this.auditFilterProviewName = auditFilterProviewName;
    }

    public String getAuditFilterTitleId()
    {
        return auditFilterTitleId;
    }

    public void setAuditFilterTitleId(final String auditFilterTitleId)
    {
        this.auditFilterTitleId = auditFilterTitleId;
    }

    public String getLibraryFilterProviewName()
    {
        return libraryFilterProviewName;
    }

    public void setLibraryFilterProviewName(final String libraryFilterProviewName)
    {
        this.libraryFilterProviewName = libraryFilterProviewName;
    }

    public String getLibraryFilterTitleId()
    {
        return libraryFilterTitleId;
    }

    public void setLibraryFilterTitleId(final String libraryFilterTitleId)
    {
        this.libraryFilterTitleId = libraryFilterTitleId;
    }

    public String getJobSummaryFilterProviewName()
    {
        return jobSummaryFilterProviewName;
    }

    public void setJobSummaryFilterProviewName(final String jobSummaryFilterProviewName)
    {
        this.jobSummaryFilterProviewName = jobSummaryFilterProviewName;
    }

    public String getJobSummaryFilterTitleId()
    {
        return jobSummaryFilterTitleId;
    }

    public void setJobSummaryFilterTitleId(final String jobSummaryFilterTitleId)
    {
        this.jobSummaryFilterTitleId = jobSummaryFilterTitleId;
    }

    public String getGroupFilterName()
    {
        return groupFilterName;
    }

    public void setGroupFilterName(final String groupFilterName)
    {
        this.groupFilterName = groupFilterName;
    }

    public String getGroupFilterId()
    {
        return groupFilterId;
    }

    public void setGroupFilterId(final String groupFilterId)
    {
        this.groupFilterId = groupFilterId;
    }

    public List<String> getEmails()
    {
        return emails;
    }

    public void setEmails(final List<String> emails)
    {
        this.emails = emails;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getURL()
    {
        String url = "";

        switch (startPage)
        {
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
        case GROUP_LIST:
            url = WebConstants.MVC_PROVIEW_GROUPS;
            break;
        default:
            url = WebConstants.MVC_BOOK_LIBRARY_LIST;
            break;
        }

        return url;
    }
}
