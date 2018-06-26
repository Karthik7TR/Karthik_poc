package com.thomsonreuters.uscl.ereader.quality.helper;

import static org.apache.commons.io.FilenameUtils.getBaseName;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.quality.model.request.CompareUnit;
import com.thomsonreuters.uscl.ereader.quality.model.request.JsonRequest;
import com.thomsonreuters.uscl.ereader.quality.model.request.ReportType;
import com.thomsonreuters.uscl.ereader.quality.model.request.Request;
import com.thomsonreuters.uscl.ereader.quality.model.response.Report;
import com.thomsonreuters.uscl.ereader.quality.model.response.ReportSourcePair;
import com.thomsonreuters.uscl.ereader.quality.model.response.Response;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class QualityUtil {
    private static final ReportType[] REPORT_TYPES = {new ReportType("text", "false", "html")};
    private String ftpStoragePath;

    @Autowired
    public QualityUtil(@Value("${xpp.quality.ftp.storage}") final String ftpStoragePath) {
        this.ftpStoragePath = ftpStoragePath;
    }

    public JsonRequest createJsonRequest(final List<CompareUnit> compareUnitList, final String emails) {
        final List<Request> requests = compareUnitList.stream()
                .map(compareUnit -> new Request(getFilename(compareUnit.getSource()), getFilename(compareUnit.getTarget()), REPORT_TYPES))
                .collect(Collectors.collectingAndThen(Collectors.toList(), requestList -> {
                    if (!StringUtils.isEmpty(emails)) {
                        requestList.add(new Request(emails));
                    }
                    return requestList;
                }));
        return new JsonRequest(requests.toArray(new Request[0]));
    }

    private String getFilename(final String filePath) {
        final Path path = Paths.get(filePath);
        return ftpStoragePath + path.getFileName().toString();
    }

    public ReportSourcePair mapToReportSourcePair(final Report report, final Response response) {
        return new ReportSourcePair(report.getReportFile(), response.getSourceFile());
    }

    public String getLocalPath(final ReportSourcePair reportSourcePair, final String reportsDir) {
        return String.format("%s\\%s.html",
                reportsDir,
                getBaseName(reportSourcePair.getSourceFile()));
    }
}
