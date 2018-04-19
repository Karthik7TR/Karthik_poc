package com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences;

import java.util.List;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceEmailService;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceEmailServiceImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AutoPopulatingList;

@Getter
@Setter
@ToString
public class UserPreferencesForm {
    public static final String FORM_NAME = "preferencesForm";

    public enum HomepageProperty {
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
    private UserPreferenceEmailService userPreferenceEmailService;

    public UserPreferencesForm() {
        startPage = HomepageProperty.LIBRARY;
        emails = new AutoPopulatingList<>(String.class);
        userPreferenceEmailService = new UserPreferenceEmailServiceImpl();
    }

    public void load(final UserPreference preference) {
        if (preference != null) {
            final String startPageStr = preference.getStartPage();

            if (StringUtils.isNotBlank(startPageStr)) {
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
            groupFilterName = preference.getGroupListGroupName();
            groupFilterId = preference.getGroupListGroupId();
            emails = userPreferenceEmailService.getEmailsString(preference);
        }
    }

    public UserPreference makeUserPreference() {
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
        preference.setEmails(StringUtils.join(emails, ","));
        return preference;
    }

    public String getURL() {
        switch (startPage) {
        case PROVIEW_LIST:
            return WebConstants.MVC_PROVIEW_TITLES;
        case AUDIT:
            return WebConstants.MVC_BOOK_AUDIT_LIST;
        case JOBS:
            return WebConstants.MVC_JOB_SUMMARY;
        case QUEUED:
            return WebConstants.MVC_JOB_QUEUE;
        case ADMINISTRATION:
            return WebConstants.MVC_ADMIN_MAIN;
        case GROUP_LIST:
            return WebConstants.MVC_PROVIEW_GROUPS;
        default:
            return WebConstants.MVC_BOOK_LIBRARY_LIST;
        }
    }
}
