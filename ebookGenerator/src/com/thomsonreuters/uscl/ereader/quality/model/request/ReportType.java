package com.thomsonreuters.uscl.ereader.quality.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportType {
    private String type;
    private String inLine;
    private String format;
}
