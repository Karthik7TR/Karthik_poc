package com.thomsonreuters.uscl.ereader.quality.model.request;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@JsonInclude(NON_NULL)
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Request {
    private String sourceFile;
    private String targetFile;
    private ReportType[] reportTypes;
    @NonNull
    private String email;

    public Request(final String sourceFile, final String targetFile, final ReportType[] reportTypes) {
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
        this.reportTypes = reportTypes;
    }
}
