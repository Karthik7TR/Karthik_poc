/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The required header in JSON format for the HTTP request body.
 * Example: "Header":{"ProductIdentifier":"Westlaw","UserHostIpAddress":"209.85.225.106"}
 */
public class Header {
	
	private String authenticationToken;
	private String contextualInformation;
	private String productIdentifier;  // like "ebookGenerator"
	private String sessionToken;
	private String slideInformation;
	private String userHostIpAddress;
	private String version;

	@JsonProperty("AuthenticationToken")
	public String getAuthenticationToken() {
		return authenticationToken;
	}
	@JsonProperty("ContextualInformation")
	public String getContextualInformation() {
		return contextualInformation;
	}
	@JsonProperty("ProductIdentifier")
	public String getProductIdentifier() {
		return productIdentifier;
	}
	@JsonProperty("SessionToken")
	public String getSessionToken() {
		return sessionToken;
	}
	@JsonProperty("SlideInformation")
	public String getSlideInformation() {
		return slideInformation;
	}
	@JsonProperty("UserHostIpAddress")
	public String getUserHostIpAddress() {
		return userHostIpAddress;
	}
	@JsonProperty("Version")
	public String getVersion() {
		return version;
	}
	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}
	public void setContextualInformation(String contextualInformation) {
		this.contextualInformation = contextualInformation;
	}
	public void setProductIdentifier(String productIdentifier) {
		this.productIdentifier = productIdentifier;
	}
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}
	public void setSlideInformation(String slideInformation) {
		this.slideInformation = slideInformation;
	}
	public void setUserHostIpAddress(String dottedDecimalIpAddress) {
		this.userHostIpAddress = dottedDecimalIpAddress;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
