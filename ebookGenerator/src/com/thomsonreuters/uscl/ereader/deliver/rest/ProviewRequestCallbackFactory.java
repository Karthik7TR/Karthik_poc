/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.rest;


/**
 * A factory to serve up instances of ProviewRequestCallback objects.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 */
public class ProviewRequestCallbackFactory {
	public ProviewRequestCallback getRequestCallback() {
		return new ProviewRequestCallback();
	}
}
