package com.thomsonreuters.uscl.ereader.quality.service;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service("reportFileHandlingService")
public class ReportFileHandlingServiceImpl implements ReportFileHandlingService {
    private static final String VALUE_GROUP = "valGr";
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile(String.format(
        ".*<tr>[\t \n]*<td><b>Source File Name</b></td>[\t \n]*<td>(?<%s>[A-Za-z0-9_\\-.()]+)</td>[\t \n]*</tr>.*", VALUE_GROUP));
    private static final Pattern PERCENTAGE_PATTERN = Pattern.compile(String.format(
        ".*<tr>[\t \n]*<td><b>Matching Percentage</b></td>[\t \n]*<td>(?<%s>[0-9\\%s.]+)</td>[\t \n]*</tr>.*", VALUE_GROUP, "%"));

    @Override
    @SneakyThrows
    public String extractParameter(@NotNull final File file, @NotNull final ReportFileParameter parameter) {
        final Pattern pattern = getPattern(parameter);
        final String reportFileContent = StringUtils.substringBefore(
            FileUtils.readFileToString(file).replaceAll("\n", "").replaceAll("\t", "").replaceAll("\r", ""), "</table>");
        final Matcher matcher = pattern.matcher(reportFileContent);
        if (!matcher.find()) {
            throw new UnsupportedOperationException(
                "Report file doesn't contain required parameter or has a different structure");
        }
        return matcher.group(VALUE_GROUP);
    }

    private Pattern getPattern(@NotNull final ReportFileParameter parameter) {
        final Pattern pattern;
        switch (parameter) {
            case FILE_NAME:
                pattern = FILE_NAME_PATTERN;
                break;
            case PERCENTAGE:
                pattern = PERCENTAGE_PATTERN;
                break;
            default:
                throw new UnsupportedOperationException(
                    String.format("Unsupported report file parameter found: %s", parameter));
        }
        return pattern;
    }

    @Override
    public Map<String, String> getFilesMatchingPercentage(@NotNull final Collection<File> files) {
        return files.stream()
            .collect(Collectors.toMap(
                file -> extractParameter(file, ReportFileParameter.FILE_NAME),
                file -> extractParameter(file, ReportFileParameter.PERCENTAGE)));
    }
}
