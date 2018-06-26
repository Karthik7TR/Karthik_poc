package com.thomsonreuters.uscl.ereader.quality.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.quality.helper.FtpManager;
import com.thomsonreuters.uscl.ereader.quality.helper.QualityUtil;
import com.thomsonreuters.uscl.ereader.quality.model.response.JsonResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@NoArgsConstructor
public class ReportServiceImpl implements ReportService {
    private FtpManager ftpManager;
    private QualityUtil qualityUtil;

    @Autowired
    public ReportServiceImpl(final FtpManager ftpManager, final QualityUtil qualityUtil) {
        this.ftpManager = ftpManager;
        this.qualityUtil = qualityUtil;
    }

    @Override
    public List<File> getReports(final JsonResponse dtResponse, final String reportsDir) {
        ftpManager.connect();
        List<File> reports = null;
        try {
            reports = Arrays.stream(dtResponse.getResponses())
                .flatMap(response -> Arrays.stream(response.getReports())
                    .map(report -> qualityUtil.mapToReportSourcePair(report, response)))
                .map(
                    reportSourcePair -> ftpManager.downloadFile(
                        reportSourcePair.getReportFile(),
                        qualityUtil.getLocalPath(reportSourcePair, reportsDir)))
                .collect(Collectors.toList());
        } finally {
            ftpManager.disconnect();
        }
        return reports;
    }
}
