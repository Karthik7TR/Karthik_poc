/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.services;

import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

/**
 * Factory to create the main Novus API object for communicating with Novus
 * to get document data.
 */
public class NovusFactoryImpl implements NovusFactory {
	
	private NovusEnvironment novusEnvironment;
	private String productName;
	private String businessUnit;
	
	public Novus createNovus(boolean isFinalStage) throws NovusException  {
		Novus novus = new Novus();
		novus.setQueueCriteria(null, novusEnvironment.toString());
		novus.setResponseTimeout(30000);
		
		if(isFinalStage) {
			novus.useLatestPit();
		} else {
			novus.createRPit();
		}
		novus.setProductName(productName);
		novus.setBusinessUnit(businessUnit);
		return novus;
	}

	@Required
	public void setNovusEnvironment(NovusEnvironment env) {
		this.novusEnvironment = env;
	}
	@Required
	public void setProductName(String productName) {
		this.productName = productName;
	}
	@Required
	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}
}
