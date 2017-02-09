package com.thomsonreuters.uscl.ereader.mgr.library.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort;

/**
 * Service methods that are common to both the Spring Batch generator engine and
 * manager web applications.
 */
public interface LibraryListService
{
    /**
     * Returns all the current book definitions based on the search criterion
     *
     * @return a list of LibraryList
     */
    List<LibraryList> findBookDefinitions(LibraryListFilter filter, LibraryListSort sort);

    /**
     * Returns a count of the current book definitions.
     * @return an long
     */
    Integer numberOfBookDefinitions(LibraryListFilter filter);
}
