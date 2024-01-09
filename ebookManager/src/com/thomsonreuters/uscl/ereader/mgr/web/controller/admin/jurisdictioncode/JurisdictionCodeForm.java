package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jurisdictioncode;

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;

public class JurisdictionCodeForm {
    //private static final Logger log = LogManager.getLogger(EditBookDefinitionForm.class);
    public static final String FORM_NAME = "jurisdictionCodeForm";

    private Long id;
    private String name;

    public JurisdictionCodeForm() {
        super();
    }

    public void initialize(final JurisTypeCode code) {
        id = code.getId();
        name = code.getName();
    }

    public JurisTypeCode makeCode() {
        final JurisTypeCode code = new JurisTypeCode();
        code.setId(id);
        code.setName(name);

        return code;
    }

    public Long getJurisId() {
        return id;
    }

    public void setJurisId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
