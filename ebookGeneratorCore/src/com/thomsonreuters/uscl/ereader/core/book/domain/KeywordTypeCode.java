/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class KeywordTypeCode implements Serializable {
	private static final long serialVersionUID = -5549776655019904722L;
	//private static final Logger log = Logger.getLogger(Author.class);
	
	private Integer codeId;
	private String codeName;
	private List<KeywordTypeValue> values = new ArrayList<KeywordTypeValue>();
	
	public KeywordTypeCode() {
		super();
	}
	

	public Integer getCodeId() {
		return codeId;
	}


	public void setCodeId(Integer codeId) {
		this.codeId = codeId;
	}


	public String getCodeName() {
		return codeName;
	}


	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}


	public List<KeywordTypeValue> getValues() {
		return values;
	}


	public void setValues(List<KeywordTypeValue> values) {
		this.values = values;
	}


	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
