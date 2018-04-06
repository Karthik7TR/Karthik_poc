package com.thomsonreuters.uscl.ereader.xpp.transformation.step;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;

/**
 * Basic XPP transformation step
 */
public abstract class XppTransformationStep extends BookStepImpl implements XppBookStep {
    @Resource(name = "transformerBuilderFactory")
    protected TransformerBuilderFactory transformerBuilderFactory;
    @Resource(name = "xslTransformationService")
    protected XslTransformationService transformationService;
    @Resource(name = "xppFormatFileSystem")
    protected XppFormatFileSystem fileSystem;

    private SortedMap<Integer, List<XppBundle>> splitParts;

    @Override
    public ExitStatus executeStep() throws Exception {
        executeTransformation();
        return ExitStatus.COMPLETED;
    }

    @NotNull
    @Override
    public List<XppBundle> getXppBundles() {
        final Object bundles = getJobExecutionContext().get(JobParameterKey.XPP_BUNDLES);
        return bundles == null ? Collections.<XppBundle>emptyList() : (List<XppBundle>) bundles;
    }

    @NotNull
    @Override
    public Map<Integer, List<XppBundle>> getSplitPartsBundlesMap() {
        if (splitParts == null) {
            splitParts = new TreeMap<>();
            splitParts.put(1, new ArrayList<>());

            getBookDefinition().getPrintComponents().stream()
            .sorted(Comparator.comparing(PrintComponent::getComponentOrder))
            .forEach(printComponent -> {
                Integer currentSplitPartNumber = splitParts.lastKey();
                if (printComponent.getSplitter()) {
                    splitParts.computeIfAbsent(++currentSplitPartNumber, ArrayList::new);
                } else {
                    splitParts.get(currentSplitPartNumber).add(getBundleByMaterial(printComponent.getMaterialNumber()));
                }
            });
        }
        return splitParts;
    }

    @NotNull
    @Override
    public XppBundle getBundleByMaterial(@NotNull final String material) {
        return getXppBundles().stream()
            .filter(bundle -> bundle.getMaterialNumber().equals(material))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(
                String.format("Material %s doesn't exist for book definition %s", material, getBookDefinition().getTitleId())));
    }

    /**
     * Implements all transformation actions
     */
    public abstract void executeTransformation() throws Exception;
}
