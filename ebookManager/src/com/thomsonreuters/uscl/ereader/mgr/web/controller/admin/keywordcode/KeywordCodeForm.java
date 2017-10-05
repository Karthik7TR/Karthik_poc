package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;

public class KeywordCodeForm {
    //private static final Logger log = LogManager.getLogger(KeywordCodeForm.class);
    public static final String FORM_NAME = "KeywordCodeForm";

    private Long id;
    private String name;
    private boolean isRequired;

    public KeywordCodeForm() {
        super();
    }

    public void initialize(final KeywordTypeCode code) {
        id = code.getId();
        name = code.getName();
        isRequired = code.getIsRequired();
    }

    public KeywordTypeCode makeCode() {
        final KeywordTypeCode code = new KeywordTypeCode();
        code.setId(id);
        code.setName(name);
        code.setIsRequired(isRequired);

        return code;
    }

    public Long getCodeId() {
        return id;
    }

    public void setCodeId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(final boolean isRequired) {
        this.isRequired = isRequired;
    }
}
