package com.thomsonreuters.uscl.ereader.quality.service;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public interface ReportFileHandlingService {
     String extractParameter(@NotNull File file, @NotNull ReportFileParameter parameter);

     Map<String, String> getFilesMatchingPercentage(@NotNull Collection<File> files);

     enum ReportFileParameter {
         PERCENTAGE, FILE_NAME;
     }
}
