package com.thomsonreuters.uscl.ereader.xpp.transformation.step;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class VolumeNumberAwareXppTransformationStep extends XppTransformationStep {
    private static final String VOLUME_GROUP = "vol";
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private static final Pattern FILE_NAME_VOLUME_PATTERN = Pattern.compile(String.format("(?:-|_)(Vol|vol|Volume|volume)_(?<%s>\\d+)", VOLUME_GROUP));

    @Autowired
    private XppGatherFileSystem xppGatherFileSystem;

    protected Integer getVolumeNumber(final XppBundle bundle) {
        final List<XppBundle> bundles = getXppBundles();
        final List<XppBundle> volumes = bundles.stream()
            .filter(currentBundle -> !currentBundle.isPocketPartPublication())
            .collect(Collectors.toList());

        final Integer currentBundleIndex;
        if (bundle.isPocketPartPublication()) {
            final Integer index = IntStream.range(0, bundles.size())
                .filter(i -> bundle.equals(bundles.get(i)))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
            currentBundleIndex = getVolumeNumber(bundles.get(index - 1));
        } else {
            currentBundleIndex = IntStream.range(0, volumes.size())
            .filter(i -> bundle.equals(volumes.get(i)))
            .map(i -> ++i)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
        }
        return currentBundleIndex;
    }

    public String getVolumeNumberByFileName(final String fileName) {
        return Optional.of(FILE_NAME_VOLUME_PATTERN.matcher(fileName))
            .filter(Matcher::find)
            .map(matcher -> matcher.group(VOLUME_GROUP))
            .orElse(StringUtils.EMPTY);
    }

    protected Optional<File> getSegOutlineFile() {
        return getXppBundles().stream()
            .map(XppBundle::getMaterialNumber)
            .map(materialNumber -> xppGatherFileSystem.getSegOutlineFile(this, materialNumber))
            .filter(Objects::nonNull)
            .filter(File::exists)
            .findAny();
    }

    protected long getNumberOfVolumes() {
        long number = getXppBundles().size();
        if (getSegOutlineFile().isPresent()) {
            number = getXppBundles().stream()
                .map(XppBundle::getVolumes)
                .flatMap(COMMA_PATTERN::splitAsStream)
                .distinct()
                .count();
        }
        return number;
    }
}
