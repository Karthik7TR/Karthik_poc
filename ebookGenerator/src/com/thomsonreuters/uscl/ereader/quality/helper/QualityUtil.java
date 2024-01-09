package com.thomsonreuters.uscl.ereader.quality.helper;

import static org.apache.commons.io.FilenameUtils.getBaseName;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.quality.domain.request.CompareUnit;
import com.thomsonreuters.uscl.ereader.quality.domain.request.JsonRequest;
import com.thomsonreuters.uscl.ereader.quality.domain.request.ReportType;
import com.thomsonreuters.uscl.ereader.quality.domain.request.Request;
import com.thomsonreuters.uscl.ereader.quality.domain.response.Report;
import com.thomsonreuters.uscl.ereader.quality.domain.response.ReportSourcePair;
import com.thomsonreuters.uscl.ereader.quality.domain.response.Response;
import lombok.NoArgsConstructor;
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

    public JsonRequest createJsonRequest(final List<CompareUnit> compareUnitList) {
        final List<Request> requests = compareUnitList.stream()
            .map(
                compareUnit -> new Request(
                    getFilename(compareUnit.getSource()),
                    getFilename(compareUnit.getTarget()),
                    REPORT_TYPES))
            .collect(Collectors.toList());
        return new JsonRequest(requests.toArray(new Request[0]));
    }

    private String getFilename(final String filePath) {
        final Path path = Paths.get(filePath);
        return ftpStoragePath + path.getFileName()
            .toString();
    }

    public ReportSourcePair mapToReportSourcePair(final Report report, final Response response) {
        return new ReportSourcePair(report.getReportFile(), response.getSourceFile());
    }

    public String getLocalPath(final ReportSourcePair reportSourcePair, final String reportsDir) {
        return String.format("%s\\%s.html", reportsDir, getBaseName(reportSourcePair.getSourceFile()));
    }
}
