/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.services;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

/**
 * Factory to create the main Novus API object for communicating with Novus
 * to get document data.
 */
public interface NovusFactory {
	
	/**
	 * Create the Novus system connection.
	 * @param isFinalStage determines to retrieve content from Final or Review stage
	 */
	public Novus createNovus(boolean isFinalStage) throws NovusException;
	
	/**
	 * Which environment "Client" | "Prod" are we working with.
	 * @param env the typesafe environment name
	 */
	public void setNovusEnvironment(NovusEnvironment env);

}
