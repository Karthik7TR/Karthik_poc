/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The TraceInformation JSON object that comes back from a Image Vertical HTTP REST web service request.
 * This is a JSON object embedded in the HTTP response body.
 */
public class TraceInformation {

	private String executionType;
	private String parentGuid;
	private String product;
	private String rootGuid;
	private String serverInformation;
	private String sessionGuid;
	private String transactionGuid;
	private String userGuid;
	
	@JsonProperty("ExecutionType")
	public String getExecutionType() {
		return executionType;
	}
	@JsonProperty("ParentGuid")
	public String getParentGuid() {
		return parentGuid;
	}
	@JsonProperty("Product")
	public String getProduct() {
		return product;
	}
	@JsonProperty("RootGuid")
	public String getRootGuid() {
		return rootGuid;
	}
	@JsonProperty("ServerInformation")
	public String getServerInformation() {
		return serverInformation;
	}
	@JsonProperty("SessionGuid")
	public String getSessionGuid() {
		return sessionGuid;
	}
	@JsonProperty("TransactionGuid")
	public String getTransactionGuid() {
		return transactionGuid;
	}
	@JsonProperty("UserGuid")
	public String getUserGuid() {
		return userGuid;
	}

	public void setExecutionType(String execType) {
		this.executionType = execType;
	}
	public void setParentGuid(String parentGuid) {
		this.parentGuid = parentGuid;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public void setRootGuid(String rootGuid) {
		this.rootGuid = rootGuid;
	}
	public void setServerInformation(String serverInformation) {
		this.serverInformation = serverInformation;
	}
	public void setSessionGuid(String sessionGuid) {
		this.sessionGuid = sessionGuid;
	}
	public void setTransactionGuid(String transactionGuid) {
		this.transactionGuid = transactionGuid;
	}
	public void setUserGuid(String userGuid) {
		this.userGuid = userGuid;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
