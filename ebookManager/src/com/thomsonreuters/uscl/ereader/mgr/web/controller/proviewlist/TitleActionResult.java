package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NonNull;

@Data
public class TitleActionResult {
    @NonNull
    private List<String> titlesToUpdate;
    @NonNull
    private List<String> updatedTitles;
    private String errorMessage;

    public TitleActionResult() {
        this.titlesToUpdate = new ArrayList<>();
        this.updatedTitles = new ArrayList<>();
    }

    public boolean hasErrorMessage() {
        return errorMessage != null;
    }
}
