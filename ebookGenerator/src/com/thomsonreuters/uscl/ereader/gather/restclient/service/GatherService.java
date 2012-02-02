/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.restclient.service;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;

public interface GatherService {
	
	/**
	 * Rest client request to gather url.
	 * @param gatherTocRequest
	 * @return
	 */
	public GatherResponse getToc(GatherTocRequest gatherTocRequest);
	
	/**
	 * Rest client request to gather url.
	 * @param gatherTocRequest
	 * @return
	 */
	public GatherResponse getNort(GatherNortRequest gatherNortRequest);
	
	/**
	 * Rest client request to gather url.
	 * @param gatherDocRequest
	 * @return
	 */
	public GatherResponse getDoc(GatherDocRequest gatherDocRequest);
	

}
