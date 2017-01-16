package com.thomsonreuters.uscl.ereader.jms.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.jms.handler.EBookRequest;

public class RequestLogDaoImpl implements RequestLogDao {
// TODO create implementation once database is configured
	@Override
	public void deleteRequest(long jobRequestId) {
		
	}

	@Override
	public List<EBookRequest> findAllRequests() {
		return null;
	}

	@Override
	public EBookRequest findByRequestId(String requestId) {
		return null;
	}

	@Override
	public Long saveRequest(EBookRequest ebookRequest) {
		return null;
	}

	@Override
	public List<EBookRequest> findAllRequestsOrderBySubmitedtime() {
		return null;
	}
	
}