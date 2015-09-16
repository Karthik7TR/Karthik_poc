package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.doctypemetric;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;

public class DocTypeMetricForm {
	
	public static final String FORM_NAME = "DocTypeMetricForm";
	
	private Long id;
	private String name;
	private Integer thresholdValue;
	private Integer thresholdPercent;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public DocTypeMetricForm() {
		super();
	}
	
	public void initialize(DocumentTypeCode code) {
		this.id = code.getId();
		this.name = code.getName();
		this.thresholdValue = code.getThresholdValue();
		this.thresholdPercent = code.getThresholdPercent();
	}
	
	public DocumentTypeCode makeCode(DocumentTypeCode code) {
		code.setThresholdValue(thresholdValue);
		code.setThresholdPercent(thresholdPercent);
		return code;
	}	
	
	public Integer getThresholdValue() {
		return thresholdValue;
	}
	public void setThresholdValue(Integer thresholdValue) {
		this.thresholdValue = thresholdValue;
	}
	public Integer getThresholdPercent() {
		return thresholdPercent;
	}
	public void setThresholdPercent(Integer thresholdPercent) {
		this.thresholdPercent = thresholdPercent;
	}

}
