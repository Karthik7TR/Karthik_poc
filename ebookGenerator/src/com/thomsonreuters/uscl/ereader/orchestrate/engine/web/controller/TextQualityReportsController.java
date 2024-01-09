package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TextQualityReportsController {
    @Autowired
    @Qualifier("formatFileSystem")
    private FormatFileSystem formatFileSystem;

    @SneakyThrows
    @RequestMapping(WebConstants.URI_GET_QUALITY_REPORT)
    public void getTextQualityReport(@PathVariable final String jobInstanceId,
                                     @PathVariable final String material,
                                     @PathVariable final String fileName,
                                     final HttpServletResponse response) {
        final File formatDirectory = formatFileSystem.getFormatDirectory(Long.valueOf(jobInstanceId));
        final File reportsDirectory = formatDirectory.toPath()
            .resolve(XppFormatFileSystemDir.QUALITY_DIR.getDirName())
            .resolve(material)
            .resolve("reports")
            .toFile();
        final File reportFile = new File(reportsDirectory, String.format("%s.html", fileName));
        if (!reportFile.exists()) {
            response.getOutputStream().print(String.format("Report file %s for job %s doesn't exist", fileName, jobInstanceId));
            response.setStatus(HttpStatus.GONE.value());
        } else {
            response.getOutputStream().write(FileUtils.readFileToByteArray(reportFile));
            response.setStatus(HttpStatus.OK.value());
        }
        response.getOutputStream().flush();
    }
}
