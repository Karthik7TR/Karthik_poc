package com.thomsonreuters.uscl.ereader.mgr.library.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.mgr.library.dao.LibraryListDao;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * Library list service
 */
public class LibraryListServiceImpl implements LibraryListService {
    //private static final Logger log = LogManager.getLogger(LibraryListServiceImpl.class);

    private LibraryListDao libraryListDao;

    /**
     * Returns all the current book definitions based on the search criterion
     *
     * @return a list of LibraryList
     */
    @Override
    @Transactional(readOnly = true)
    public List<LibraryList> findBookDefinitions(final LibraryListFilter filter, final LibraryListSort sort) {
        return libraryListDao.findBookDefinitions(filter, sort);
    }

    /**
     * Returns a count of the current book definitions.
     * @return an long
     */
    @Override
    @Transactional(readOnly = true)
    public Integer numberOfBookDefinitions(final LibraryListFilter filter) {
        return libraryListDao.numberOfBookDefinitions(filter);
    }

    @Required
    public void setLibraryListDao(final LibraryListDao dao) {
        libraryListDao = dao;
    }
}
