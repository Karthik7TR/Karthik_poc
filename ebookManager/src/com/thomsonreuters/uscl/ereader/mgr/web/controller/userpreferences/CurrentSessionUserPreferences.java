package com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences;

import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditFilterForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.UserPreferencesForm.HomepageProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class CurrentSessionUserPreferences {
    public static final String NAME = "currentSessionUserPreferences";

    private ProviewAuditFilterForm proviewAuditPreferences = new ProviewAuditFilterForm();
    private HomepageProperty startPage;
    private String auditFilterProviewName;
    private String auditFilterTitleId;
    private String libraryFilterProviewName;
    private String libraryFilterTitleId;
    private String jobSummaryFilterProviewName;
    private String jobSummaryFilterTitleId;
    private String groupFilterName;
    private String groupFilterId;

    public CurrentSessionUserPreferences(@NotNull final UserPreferencesForm form) {
        BeanUtils.copyProperties(form, this);
    }

    public String getUri() {
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
                final Map<String, String> queryParameters = new HashMap<>();
                queryParameters.put(WebConstants.KEY_GROUP_FILTER_NAME, getGroupFilterName());
                queryParameters.put(WebConstants.KEY_GROUP_FILTER_ID, getGroupFilterId());
                return generateUri(WebConstants.MVC_PROVIEW_GROUPS, queryParameters);
            default:
                return WebConstants.MVC_BOOK_LIBRARY_LIST;
        }
    }

    private String generateUri(final String path, @NotNull final Map<String, String> queryParameters) {
        final URIBuilder uri = new URIBuilder().setPath(path);
        queryParameters.entrySet().stream()
            .filter(param -> param.getValue() != null)
            .forEach(param -> uri.addParameter(param.getKey(), param.getValue()));

        return uri.toString();
    }
}
