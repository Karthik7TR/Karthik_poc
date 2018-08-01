package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.group;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import org.springframework.stereotype.Component;

@Component
public class FileGroupHelper {
    private static final String PUB_GROUP = "pbGrp";
    private static final Pattern BASE_NAME_PATTERN = Pattern.compile(String.format("\\d+-(?<%s>[A-Za-z0-9_]+?_\\d*)_?[A-Za-z0-9]*\\.DIVXML\\.xml", PUB_GROUP));
    private static final String SELF_PATTERN = "\\d+-%s\\.DIVXML\\.xml";
    private static final String CHILD_PATTERN = "\\d+-%s_[A-Za-z0-9]+\\.DIVXML\\.xml";

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

    private Stream<String> groupStream(final String fileName, final XppBundle bundle) {
        final Matcher matcher = BASE_NAME_PATTERN.matcher(fileName);
        if (matcher.find()) {
            final String baseName = matcher.group(PUB_GROUP);
            return bundle.getOrderedFileList().stream()
                    .filter(file -> file.matches(String.format(SELF_PATTERN, baseName)) || file.matches(String.format(CHILD_PATTERN, baseName)));
        } else {
            return Stream.empty();
        }
    }
}
