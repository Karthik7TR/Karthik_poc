package com.thomsonreuters.uscl.ereader.quality.service;

import java.io.File;
import java.util.List;

import com.thomsonreuters.uscl.ereader.quality.model.response.JsonResponse;

public interface ReportService {
    List<File> getReports(JsonResponse dtResponse, String reportsDir);
}
