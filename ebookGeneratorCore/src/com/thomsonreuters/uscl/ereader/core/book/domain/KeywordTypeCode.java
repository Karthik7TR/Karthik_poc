/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class KeywordTypeCode implements Serializable {
	private static final long serialVersionUID = -5549776655019904722L;
	//private static final Logger log = Logger.getLogger(Author.class);
	
	private Long codeId;
	private String codeName;
	private Collection<KeywordTypeValue> values = new ArrayList<KeywordTypeValue>();
	
	public KeywordTypeCode() {
		super();
	}
	

	public Long getCodeId() {
		return codeId;
	}


	public void setCodeId(Long codeId) {
		this.codeId = codeId;
	}


	public String getCodeName() {
		return codeName;
	}


	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}


	public Collection<KeywordTypeValue> getValues() {
		return values;
	}


	public void setValues(Collection<KeywordTypeValue> values) {
		this.values = values;
	}


	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
