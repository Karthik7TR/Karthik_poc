package com.thomsonreuters.uscl.ereader.quality.domain.email;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QualityReportEmail {
    private List<InternetAddress> recipients;
    private Map<String, List<File>> reports;
    private String titleId;
    private long jobId;
    private String environment;
    private String host;
    private String port;
}
