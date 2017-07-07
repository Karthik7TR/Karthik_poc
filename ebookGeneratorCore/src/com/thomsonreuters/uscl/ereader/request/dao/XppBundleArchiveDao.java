package com.thomsonreuters.uscl.ereader.request.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;

public interface XppBundleArchiveDao
{
    /**
     * Add a bundle to the archive
     * @param ebookRequestId
     */
    Long saveRequest(XppBundleArchive ebookRequest);

    /**
     * Find a Phoenix ebook bundle object by its primary key
     * @param requestId the primary key
     * @return the object found or null if not found
     */
    XppBundleArchive findByPrimaryKey(long ebookRequestId);

    /**
     * Remove a bundle from the archive
     * @param ebookRequestId
     */
    void deleteRequest(long ebookRequestId);

    /**
     * Find a Phoenix ebook bundle object by its ID
     * @param requestId the primary key
     * @return the object found or null if not found
     */
    XppBundleArchive findByRequestId(String requestId);

    /**
     * Find a Phoenix ebook bundle object by its material number
     * @param requestId the primary key
     * @return the object found or null if not found
     */
    XppBundleArchive findByMaterialNumber(String materialNumber);

    /**
     * Returns all rows of the Phoenix bundle table, in no particular order.
     */
    List<XppBundleArchive> findAllRequests();

    /**
     * Returns rows of the Phoenix bundle table by @param material numbers list.
     */
    List<XppBundleArchive> findByMaterialNumberList(List<String> sourceMaterialNumberList);
}
