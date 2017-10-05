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

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public DocTypeMetricForm() {
        super();
    }

    public void initialize(final DocumentTypeCode code) {
        id = code.getId();
        name = code.getName();
        thresholdValue = code.getThresholdValue();
        thresholdPercent = code.getThresholdPercent();
    }

    public DocumentTypeCode makeCode(final DocumentTypeCode code) {
        code.setThresholdValue(thresholdValue);
        code.setThresholdPercent(thresholdPercent);
        return code;
    }

    public Integer getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(final Integer thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public Integer getThresholdPercent() {
        return thresholdPercent;
    }

    public void setThresholdPercent(final Integer thresholdPercent) {
        this.thresholdPercent = thresholdPercent;
    }
}
