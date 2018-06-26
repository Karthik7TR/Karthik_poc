package com.thomsonreuters.uscl.ereader.quality.model.response;

import lombok.Data;

@Data
public class Response {
    private String sourceFile;
    private String targetFile;
    private Report[] reports;
}
