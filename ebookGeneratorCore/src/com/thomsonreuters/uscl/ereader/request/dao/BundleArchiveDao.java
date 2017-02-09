package com.thomsonreuters.uscl.ereader.request.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.request.EBookRequest;

public interface BundleArchiveDao {
	
	/**
	 * Add a request to the archive
	 * @param ebookRequestId
	 */
	public Long saveRequest(EBookRequest ebookRequest);
	
	/**
	 * Find a Phoenix ebook request object by its primary key
	 * @param requestId the primary key
	 * @return the object found or null if not found
	 */
	public EBookRequest findByPrimaryKey(long ebookRequestId);

	/**
	 * Remove a request from the archive
	 * @param ebookRequestId
	 */
	public void deleteRequest(long ebookRequestId);

	/**
	 * Find a Phoenix ebook request object by its ID
	 * @param requestId the primary key
	 * @return the object found or null if not found
	 */
	public EBookRequest findByRequestId(String requestId);

	/**
	 * Returns all rows of the Phoenix request table, in no particular order.
	 */
	public List<EBookRequest> findAllRequests();

}