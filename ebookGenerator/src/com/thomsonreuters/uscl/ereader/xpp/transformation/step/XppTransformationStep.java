package com.thomsonreuters.uscl.ereader.xpp.transformation.step;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;

/**
 * Basic XPP transformation step
 */
public abstract class XppTransformationStep extends BookStepImpl implements XppBookStep
{
    @Resource(name = "transformerBuilderFactory")
    protected TransformerBuilderFactory transformerBuilderFactory;
    @Resource(name = "xslTransformationService")
    protected XslTransformationService transformationService;
    @Resource(name = "xppFormatFileSystem")
    protected XppFormatFileSystem fileSystem;

    @Override
    public ExitStatus executeStep() throws Exception
    {
        executeTransformation();
        return ExitStatus.COMPLETED;
    }

    @NotNull
    @Override
    public List<XppBundle> getXppBundles()
    {
        final Object bundles = getJobExecutionContext().get(JobParameterKey.XPP_BUNDLES);
        return bundles == null ? Collections.<XppBundle>emptyList() : (List<XppBundle>) bundles;
    }

    /**
     * Implements all transformation actions
     */
    public abstract void executeTransformation() throws Exception;
}
