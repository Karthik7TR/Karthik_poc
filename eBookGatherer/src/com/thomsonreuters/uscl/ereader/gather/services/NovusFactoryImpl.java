/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.services;

import org.springframework.beans.factory.annotation.Required;

import com.westgroup.novus.productapi.Novus;

/**
 * Factory to create the main Novus API object for communicating with Novus
 * to get document data.
 */
public class NovusFactoryImpl implements NovusFactory {
	
	private String novusEnvironment;  // "Client" | "Prod"
	private String productName;
	private String businessUnit;
	
	public Novus createNovus() {
		Novus novus = new Novus();
		novus.setQueueCriteria(null, novusEnvironment);
		novus.setResponseTimeout(30000);
		novus.useLatestPit();
		novus.setProductName(productName);
		novus.setBusinessUnit(businessUnit);
		return novus;
	}

	@Required
	public void setNovusEnvironment(String envName) {
		this.novusEnvironment = envName;
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
