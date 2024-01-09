package com.thomsonreuters.uscl.ereader.quality.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class Request {
    private String sourceFile;
    private String targetFile;
    private ReportType[] reportTypes;
}
