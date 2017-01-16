package com.thomsonreuters.uscl.ereader.jms.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.jms.handler.EBookRequest;

public interface RequestLogDao {
	
	public void deleteRequest(long jobRequestId);

	/**
	 * Returns all rows of the Phoenix request table, in no particular order.
	 */
	public List<EBookRequest> findAllRequests();

	/**
	 * Find a Phoenix ebook request object by its ID
	 * @param requestId the primary key
	 * @return the object found or null if not found
	 */
	public EBookRequest findByRequestId(String requestId);
		
	public Long saveRequest(EBookRequest ebookRequest);
	
	public List<EBookRequest> findAllRequestsOrderBySubmitedtime();

}