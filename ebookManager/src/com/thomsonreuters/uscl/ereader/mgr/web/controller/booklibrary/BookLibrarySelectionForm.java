package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BookLibrarySelectionForm {
    public enum Command {
        IMPORT,
        EXPORT,
        GENERATE,
        PROMOTE
    }

    public enum DisplayTagSortProperty {
        PROVIEW_DISPLAY_NAME,
        SOURCE_TYPE,
        TITLE_ID,
        LAST_GENERATED_DATE,
        DEFINITION_STATUS,
        LAST_EDIT_DATE
    };

    public static final String FORM_NAME = "bookLibrarySelectionForm";

    private String[] selectedEbookKeys;
    private Command command;
    private PageAndSort<DisplayTagSortProperty> pageAndSort = new PageAndSort<>(); // sort, page, dir, objectsPerPage

    public Command getCommand() {
        return command;
    }

    public String getDir() {
        return (pageAndSort.isAscendingSort()) ? "asc" : "desc";
    }

    public Integer getObjectsPerPage() {
        return pageAndSort.getObjectsPerPage();
    }

    public Integer getPage() {
        return pageAndSort.getPageNumber();
    }

    public String[] getSelectedEbookKeys() {
        return selectedEbookKeys;
    }

    public DisplayTagSortProperty getSort() {
        return pageAndSort.getSortProperty();
    }

    public boolean isAscendingSort() {
        return pageAndSort.isAscendingSort();
    }

    public void setCommand(final Command cmd) {
        command = cmd;
    }

    public void setDir(final String direction) {
        pageAndSort.setAscendingSort("asc".equals(direction));
    }

    public void setObjectsPerPage(final Integer objectsPerPage) {
        pageAndSort.setObjectsPerPage(objectsPerPage);
    }

    public void setPage(final Integer pageNumber) {
        pageAndSort.setPageNumber(pageNumber);
    }

    public void setSelectedEbookKeys(final String[] selectedEbookKeys) {
        this.selectedEbookKeys = selectedEbookKeys;
    }

    public void setSort(final DisplayTagSortProperty sortProperty) {
        pageAndSort.setSortProperty(sortProperty);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
