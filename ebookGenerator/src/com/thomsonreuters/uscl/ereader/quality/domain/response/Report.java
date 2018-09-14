package com.thomsonreuters.uscl.ereader.quality.domain.response;

import lombok.Data;

@Data
public class Report {
    private String reportType;
    private String status;
    private Object statusMessage;
    private Float percentageMatch;
    private String reportFile;
    private String reportUrl;
    private String format;
    private String inLine;
    private String abbreviate;
}
