package com.thomsonreuters.uscl.ereader.quality.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportSourcePair {
    private String reportFile;
    private String sourceFile;
}
