package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.concurrent.Callable;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TitleAction {
    private TitleActionName actionName;
    private Callable<TitleActionResult> action;
    private String emailSubjectTemplate;
    private String emailBodySuccess;
    private String emailBodyUnsuccessful;
    private String attributeSuccess;
    private String attributeUnsuccessful;
}
