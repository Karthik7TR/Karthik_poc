/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.orchestrate.engine.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

  
 /**
  * Domain objects mapped to table JOB_STARTUP_THROTTLE in schema EBOOK_CONFIG
  *  	
  * @author Mahendra Survase (u0105927)
  *
  */
@Entity
@Table(schema="EBOOK_CONFIG", name="JOB_STARTUP_THROTTLE")
public class JobStartupThrottle implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String THROTTLE_TIME_ZONE = "throttle_time_zone";
	public static String THROTTLE_STEP_NAME= "throttle_step_name";
	public static String THROTTLE_LIMIT= "throttle_limit";

    @Column(name = "THROTTLE_TIME_ZONE")
	private String throttleTimeZone;

	@Id
    @Column(name = "THROTTLE_STEP_NAME")
    private String throttleStepName;

    @Column(name = "THROTTLE_LIMIT")
    private int throttleLimit;
    
    public JobStartupThrottle()
    {
    	super();
    }
    
	public int getThrottleLimit() {
		return throttleLimit;
	}

	public void setThrottleLimit(int throttleLimit) {
		this.throttleLimit = throttleLimit;
	}
	
	public String getThrottleTimeZone() {
		return throttleTimeZone;
	}

	public void setThrottleTimeZone(String throttleTimeZone) {
		this.throttleTimeZone = throttleTimeZone;
	}

	public String getThrottleStepName() {
		return throttleStepName;
	}

	public void setThrottleStepName(String throttleStepName) {
		this.throttleStepName = throttleStepName;
	}

    

	
	
}
