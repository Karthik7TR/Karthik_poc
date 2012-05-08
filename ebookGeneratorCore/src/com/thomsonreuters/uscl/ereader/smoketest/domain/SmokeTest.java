/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */


package com.thomsonreuters.uscl.ereader.smoketest.domain;

public class SmokeTest {
	
	public String name;
	public String address;
	public boolean isRunning;

	public SmokeTest() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name; 
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setIsRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

}
