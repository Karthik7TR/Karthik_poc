package com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditFilterForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryFilterForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditFilterForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.UserPreferencesForm.HomepageProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class CurrentSessionUserPreferences implements Serializable {
    public static final long serialVersionUID = 1750487649561019L;
    public static final String NAME = "currentSessionUserPreferences";

    private ProviewAuditFilterForm proviewAuditPreferences = new ProviewAuditFilterForm();
    private BookLibraryFilterForm bookLibraryPreferences = new BookLibraryFilterForm();
    private BookAuditFilterForm bookAuditPreferences = new BookAuditFilterForm();
    private HomepageProperty startPage;
    private String auditFilterProviewName;
    private String auditFilterTitleId;
    private String libraryFilterProviewName;
    private String libraryFilterTitleId;
    private String jobSummaryFilterProviewName;
    private String jobSummaryFilterTitleId;
    private String groupFilterName;
    private String groupFilterId;
    // PROVIEW LIST page parameters
    private String proviewDisplayName;
    private String titleId;
    private String minVersions;
    private String maxVersions;
    private String proviewListObjectsPerPage;
    private String status;

    public CurrentSessionUserPreferences(@NotNull final UserPreferencesForm form) {
        BeanUtils.copyProperties(form, this);
        bookLibraryPreferences.setProviewDisplayName(getLibraryFilterProviewName());
        bookLibraryPreferences.setTitleId(getLibraryFilterTitleId());
        proviewDisplayName = form.getProviewListFilterProviewName();
        titleId = form.getProviewListFilterTitleId();
        bookAuditPreferences.setProviewDisplayName(getAuditFilterProviewName());
        bookAuditPreferences.setTitleId(getAuditFilterTitleId());
    }

    public String getUri() {
        switch (startPage) {
            case PROVIEW_LIST:
                final Map<String, String> proviewListQueryParams = new HashMap<>();
                proviewListQueryParams.put(WebConstants.KEY_PROVIEW_DISPLAY_NAME_FILTER, getProviewDisplayName());
                proviewListQueryParams.put(WebConstants.KEY_TITLE_ID_FILTER, getTitleId());
                proviewListQueryParams.put(WebConstants.KEY_MIN_VERSIONS_FILTER, getMinVersions());
                proviewListQueryParams.put(WebConstants.KEY_MAX_VERSIONS_FILTER, getMaxVersions());
                proviewListQueryParams.put(WebConstants.KEY_OBJECTS_PER_PAGE, getProviewListObjectsPerPage());
                proviewListQueryParams.put(WebConstants.KEY_STATUS, getStatus());
                return generateUri(WebConstants.MVC_PROVIEW_TITLES, proviewListQueryParams);
            case AUDIT:
                final Map<String, String> bookAuditListQueryParameters = new HashMap<>();
                bookAuditListQueryParameters.put(WebConstants.KEY_PROVIEW_DISPLAY_NAME_FILTER, getAuditFilterProviewName());
                bookAuditListQueryParameters.put(WebConstants.KEY_TITLE_ID_FILTER, getAuditFilterTitleId());
                return generateUri(WebConstants.MVC_BOOK_AUDIT_LIST, bookAuditListQueryParameters);
            case JOBS:
                return WebConstants.MVC_JOB_SUMMARY;
            case QUEUED:
                return WebConstants.MVC_JOB_QUEUE;
            case ADMINISTRATION:
                return WebConstants.MVC_ADMIN_MAIN;
            case GROUP_LIST:
                final Map<String, String> groupListQueryParameters = new HashMap<>();
                groupListQueryParameters.put(WebConstants.KEY_GROUP_FILTER_NAME, getGroupFilterName());
                groupListQueryParameters.put(WebConstants.KEY_GROUP_FILTER_ID, getGroupFilterId());
                return generateUri(WebConstants.MVC_PROVIEW_GROUPS, groupListQueryParameters);
            default:
                final Map<String, String> bookLibraryListQueryParameters = new HashMap<>();
                bookLibraryListQueryParameters.put(WebConstants.KEY_PROVIEW_DISPLAY_NAME_FILTER, getLibraryFilterProviewName());
                bookLibraryListQueryParameters.put(WebConstants.KEY_TITLE_ID_FILTER, getLibraryFilterTitleId());
                return generateUri(WebConstants.MVC_BOOK_LIBRARY_LIST, bookLibraryListQueryParameters);
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
