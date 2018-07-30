package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.group;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import org.springframework.stereotype.Component;

@Component
public class FileGroupHelper {
    private static final String PUB_GROUP = "pbGrp";
    private static final Pattern COMMON_GROUP_PATTERN = Pattern.compile(String.format("\\d+-(?<%s>[A-Za-z_]+_\\d+)_?[A-Za-z0-9]*\\.DIVXML\\.xml", PUB_GROUP));
    private static final String PARTICULAR_GROUP_PATTERN = "\\d+-%s_?[A-Za-z0-9]*\\.DIVXML\\.xml";

    public boolean isGroupPart(final String fileName, final XppBundle bundle) {
        return groupStream(fileName, bundle)
                .count() > 1;
    }

    public boolean isGroupRoot(final String fileName, final XppBundle bundle) {
        final List<String> group = groupStream(fileName, bundle)
                .collect(toList());
        return group.size() > 1 && group.get(0).equals(fileName);
    }

    public List<String> getGroupFileNames(final String fileName, final XppBundle bundle) {
        return groupStream(fileName, bundle)
                .collect(toList());
    }

    private Optional<String> getGroupRoot(final String fileName) {
        Optional<String> result = Optional.empty();
        final Matcher fileGroupMatcher = COMMON_GROUP_PATTERN.matcher(fileName);
        if (BundleFileType.getByFileName(fileName) == BundleFileType.MAIN_CONTENT && fileGroupMatcher.find()) {
            result = Optional.ofNullable(fileGroupMatcher.group(PUB_GROUP));
        }
        return result;
    }

    private Stream<String> groupStream(final String fileName, final XppBundle bundle) {
        return getGroupRoot(fileName)
                .map(groupRoot -> bundle.getOrderedFileList().stream()
                        .filter(file -> file.matches(String.format(PARTICULAR_GROUP_PATTERN, groupRoot))))
                .orElse(Stream.empty());
    }
}
