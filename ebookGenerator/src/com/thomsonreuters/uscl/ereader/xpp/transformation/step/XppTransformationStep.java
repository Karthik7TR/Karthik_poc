package com.thomsonreuters.uscl.ereader.xpp.transformation.step;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.TransformationUtil;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.springframework.batch.core.ExitStatus;

/**
 * Basic XPP transformation step
 */
public abstract class XppTransformationStep extends BookStepImpl
{
    @Resource(name = "transformerBuilderFactory")
    protected TransformerBuilderFactory transformerBuilderFactory;
    @Resource(name = "xslTransformationService")
    protected XslTransformationService transformationService;
    @Resource(name = "transformationUtil")
    protected TransformationUtil transformationUtil;
    @Resource(name = "xppFormatFileSystem")
    protected XppFormatFileSystem fileSystem;

    @Override
    public ExitStatus executeStep() throws Exception
    {
        if (transformationUtil.shouldSkip(this))
        {
            return ExitStatus.COMPLETED;
        }

        executeTransformation();
        return ExitStatus.COMPLETED;
    }

    /**
     * Implements all transformation actions
     */
    public abstract void executeTransformation() throws Exception;
}
