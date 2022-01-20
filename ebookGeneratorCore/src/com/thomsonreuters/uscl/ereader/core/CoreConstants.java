package com.thomsonreuters.uscl.ereader.core;

import org.mockito.internal.util.collections.Sets;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Set;

public class CoreConstants {
    /** The name of the current Novus environment */
    public enum NovusEnvironment {
        Client,
        Prod
    };

    public static final String SLASH = "/";
    public static final String UNDERSCORE = "_";
    public static final String DASH = "-";

    // Data directory path related info used in the InitializeTask (generator) and the cleanup ManagerService (manager).
    public static final String DATA_DIR = "data";
    // Date/Time formatting patterns
    public static final String DIR_DATE_FORMAT = "yyyyMMdd";
    public static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy"; // default date presentation
    public static final String DATE_TIME_FORMAT_PATTERN = DATE_FORMAT_PATTERN + " HH:mm:ss";
    public static final String DATE_TIME_MS_FORMAT_PATTERN = DATE_TIME_FORMAT_PATTERN + ".SSS";
    public static final String PNG = ".png";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(CoreConstants.DATE_TIME_FORMAT_PATTERN);

    /** We are putting the *.mvc suffix on the URL's because the manager app dispatcher servlet expects the URL's to follow this pattern. */
    public static final String URI_SYNC_MISC_CONFIG = "service/sync/misc/config.mvc";
    public static final String URI_SYNC_JOB_THROTTLE_CONFIG = "service/sync/job/throttle/config.mvc";
    public static final String URI_SYNC_PLANNED_OUTAGE = "service/sync/planned/outage.mvc";

    public static final String URI_GET_JOB_THROTTLE_CONFIG = "service/get/job/throttle/config";
    public static final String URI_GET_MISC_CONFIG = "service/get/misc/config";

    public static final String USCL_PUBLISHER_NAME = "uscl";
    public static final String CW_PUBLISHER_NAME = "cw";
    public static final String ALL_PUBLISHERS = "all_publishers";

    public static final String REVIEW_BOOK_STATUS = "Review";
    public static final String FINAL_BOOK_STATUS = "Final";
    public static final String REMOVED_BOOK_STATUS = "Removed";
    public static final String CLEANUP_BOOK_STATUS = "Cleanup";

    public static final String GROUP_TYPE_STANDARD = "standard";
    public static final String GROUP_TYPE_EREFERENCE = "ereference";

    public static final String ERROR_SIGN_SOME_PARTS_HAVE_INCONSISTENT_STATUS_OR_ABSENT = "^";
    public static final String ERROR_SIGN_SOME_PARTS_ARE_IN_FINAL_SOME_IN_REVIEW_STATE = "*";

    public static final Set<String> CAN_DELETE_STATUSES = Sets.newSet(CLEANUP_BOOK_STATUS, REMOVED_BOOK_STATUS);
    public static final Set<String> CAN_REMOVE_STATUSES = Sets.newSet(REVIEW_BOOK_STATUS, FINAL_BOOK_STATUS);
    public static final Set<String> CAN_PROMOTE_STATUSES = Sets.newSet(REVIEW_BOOK_STATUS, REVIEW_BOOK_STATUS + ERROR_SIGN_SOME_PARTS_ARE_IN_FINAL_SOME_IN_REVIEW_STATE);

    public static final String MVC_FRONT_MATTER_IMAGE_PREVIEW = "frontMatterImagePreview.mvc";
    public static final String MVC_FRONT_MATTER_CSS_PREVIEW = "frontMatterCssPreview.mvc";
    public static final String MVC_FRONT_MATTER_PDF_PREVIEW = "frontMatterPdfPreview.mvc";
    public static final String MVC_COVER_IMAGE = "coverImage.mvc";
    public static final String EBOOK_GENERATOR_CSS = "ebook_generator.css";

    public static final String PUBLISHED_DATE_DATEFIELD_NAME = "Published title";
    public static final String RELEASE_NOTES_HEADER = "Release notes";

    public static final String KEY_SIMPLE_REST_RESPONSE = "simpleResponse";
    public static final String KEY_DOC_GUID_TO_TOPIC_MAP = "docGuidToTopicMap";

    public static final String KEY_PROVIEW_HOST = "proviewHost";

    public static final String TITLE_ID = "titleId";
    public static final String PROVIEW_NAME = "proviewName";

    /** Marshalling view to JiBX marshal the GenerRestServiceResponse into XML that is returned to requesting clients. */
    public static final String VIEW_SIMPLE_REST_RESPONSE = "simpleResponseView";

    public static final String PROD_ENVIRONMENT_NAME = "prodcontent";

    public static final String SUBGROUP_SPLIT_ERROR_MESSAGE =
        "Subgroup name should be changed for every major version of split Book.";

    public static final String SUBGROUP_ERROR_MESSAGE =
        "No subgroup(s) in previous groups. Assign previous book versions to subgroup(s) via 'Create/Edit Group' button.";

    public static final String DUPLICATE_SUBGROUP_ERROR_MESSAGE =
        "Subgroup heading already exists for previous versions.";

    public static final String EMPTY_GROUP_ERROR_MESSAGE = "Group name cannot be empty.";

    public static final String NO_TITLE_IN_PROVIEW = "Title does not exist in Proview.";

    public static final String GROUP_METADATA_EXCEPTION = "GroupMetadataException";

    public static final String GROUP_AND_VERSION_EXISTS = "Group already exists in Proview.";

    public static final String TTILE_IN_QUEUE = "Title already exists in publishing queue";

    public static final String TTILE_STATUS_REMOVED = "Title status changed to removed";

    public static final String TITLE_PAGE_IMAGE = "titlePageImage";
}
