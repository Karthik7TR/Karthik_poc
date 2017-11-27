package com.thomsonreuters.uscl.ereader.xpp.transformation.step;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;

public abstract class VolumeNumberAwareXppTransformationStep extends XppTransformationStep {
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
}
