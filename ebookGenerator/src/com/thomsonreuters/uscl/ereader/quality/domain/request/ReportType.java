package com.thomsonreuters.uscl.ereader.quality.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportType {
    private String type;
    private String inLine;
    private String format;
}
