package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode;

import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCode;

public class StateCodeForm {
    public static final String FORM_NAME = "stateCodeForm";

    private Long id;
    private String name;

    public StateCodeForm() {
        super();
    }

    public void initialize(final StateCode code) {
        id = code.getId();
        name = code.getName();
    }

    public StateCode makeCode() {
        final StateCode code = new StateCode();
        code.setId(id);
        code.setName(name);

        return code;
    }

    public Long getStateId() {
        return id;
    }

    public void setStateId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
