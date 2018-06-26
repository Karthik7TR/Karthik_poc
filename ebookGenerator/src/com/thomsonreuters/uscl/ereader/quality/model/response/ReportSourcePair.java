package com.thomsonreuters.uscl.ereader.quality.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportSourcePair {
    private String reportFile;
    private String sourceFile;
}
