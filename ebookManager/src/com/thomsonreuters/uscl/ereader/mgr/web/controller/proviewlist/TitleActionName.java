package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum TitleActionName {
    PROMOTE("Final", "Review*"),
    REMOVE("Removed", "Review^"),
    DELETE("", "Removed^");

    private Map<OperationResult, String> resultToStatusMap;

    TitleActionName(String successfulStatus, String partiallySuccessfulStatus) {
        Map<OperationResult, String> resultToStatusMap = new HashMap<>();
        resultToStatusMap.put(OperationResult.SUCCESSFUL, successfulStatus);
        resultToStatusMap.put(OperationResult.PARTIALLY_SUCCESSFUL, partiallySuccessfulStatus);
        this.resultToStatusMap = Collections.unmodifiableMap(resultToStatusMap);
    }

    String getStatus(OperationResult operationResult, String previousStatus) {
        return resultToStatusMap.getOrDefault(operationResult, previousStatus);
    }
}
