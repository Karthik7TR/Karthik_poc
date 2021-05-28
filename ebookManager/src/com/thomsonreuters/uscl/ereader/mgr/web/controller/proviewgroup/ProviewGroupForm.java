package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import static org.apache.commons.lang3.StringUtils.isBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProviewGroupForm {
    public static final String FORM_NAME = "proviewGroupForm";

    public enum Command {
        REFRESH
    }

    private Command command;
    private String objectsPerPage;

    // filtering parameters
    private String groupFilterName;
    private String groupFilterId;

    public boolean areAllFiltersBlank() {

        return isBlank(getGroupFilterName()) && isBlank(getGroupFilterId());
    }
}
