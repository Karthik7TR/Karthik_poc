package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookaudit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.StringUtils;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;

/**
 * The form backing object that holds the data the user enters into the Audit book filter HTML form.
 */
public class AdminAuditFilterForm implements Serializable {

    private static final long serialVersionUID = -7115884046790747884L;

    public static final String FORM_NAME = "adminAuditFilterForm";

    //private static final Logger log = LogManager.getLogger(AdminAuditFilterForm.class);

    @Getter @Setter
    private Integer adminAuditListFullSize =0;

    private String titleId;
    private String proviewDisplayName;
    private String isbn;

    public AdminAuditFilterForm() {
        initialize();
    }

    /**
     * Set all values back to defaults.
     * Used in resetting the form.
     */
    public void initialize() {
        populate(null, null, null);
    }

    public void populate(final String titleId, final String proviewDisplayName, final String isbn) {
        this.titleId = titleId;
        this.proviewDisplayName = proviewDisplayName;
        this.isbn = isbn;
    }

    private static final String ASC_SORT = "asc";
    private static final String DESC_SORT = "desc";

    public enum Command {
        REFRESH
    }
       //ADDDED

    public enum DisplayTagSortProperty {
        PROVIEW_DISPLAY_NAME,
        TITLE_ID,
       PROVIEW_VERSION,
        ISBN,
        GENRATE_DATE_TIME,

    }

    @Getter @Setter
    private List<AdminAuditRecordForm> proviewTitleListFull= new ArrayList<AdminAuditRecordForm>();
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final PageAndSort<PublishingStatsSort.SortProperty> pageAndSort =
            new PageAndSort<>(1, PublishingStatsSort.SortProperty.PROVIEW_DISPLAY_NAME, true); // sort, page, dir, objectsPerPage

    public String getDir() {
        return (pageAndSort.isAscendingSort()) ? ASC_SORT : DESC_SORT;
    }

    public void setDir(final String direction) {
        pageAndSort.setAscendingSort(ASC_SORT.equals(direction));
    }

    public Integer getObjectsPerPage() {
        return pageAndSort.getObjectsPerPage();
    }

    public void setObjectsPerPage(final Integer objectsPerPage) {
        pageAndSort.setObjectsPerPage(objectsPerPage);
    }

    public Integer getPage() {
        return pageAndSort.getPageNumber();
    }

    public void setPage(final Integer pageNumber) {
        pageAndSort.setPageNumber(pageNumber);
    }

    public PublishingStatsSort.SortProperty getSort() {
        return pageAndSort.getSortProperty();
    }

    public void setSort(final PublishingStatsSort.SortProperty sortProperty) {
        pageAndSort.setSortProperty(sortProperty);
    }

    public boolean isAscendingSort() {
        return pageAndSort.isAscendingSort();
    }

        //added


    public String getProviewDisplayName() {
        return proviewDisplayName;
    }

    public String getTitleId() {
        return titleId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setProviewDisplayName(final String name) {
        proviewDisplayName = (name != null) ? name.trim() : null;
    }

    public void setTitleId(final String titleId) {
        this.titleId = (titleId != null) ? titleId.trim() : null;
    }

    public void setIsbn(final String isbn) {
        this.isbn = (isbn != null) ? isbn.trim() : null;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(titleId) && StringUtils.isEmpty(proviewDisplayName) && StringUtils.isEmpty(isbn);
    }
}
