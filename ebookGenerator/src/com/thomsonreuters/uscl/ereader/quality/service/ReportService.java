package com.thomsonreuters.uscl.ereader.quality.service;

import java.io.File;
import java.util.List;

import com.thomsonreuters.uscl.ereader.quality.domain.response.JsonResponse;

public interface ReportService {
    List<File> getReports(JsonResponse dtResponse, String reportsDir);
}
