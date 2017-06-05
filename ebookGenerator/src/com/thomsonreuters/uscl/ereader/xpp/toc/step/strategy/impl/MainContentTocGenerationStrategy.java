package com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Generates TOC of bundle main content files
 */
@Component
public class MainContentTocGenerationStrategy extends AbstractXslTocGenerationStrategy
{
    private final XppFormatFileSystem xppFormatFileSystem;
    private final File extractMainContentTocXsl;

    private final TransformationUnit[] transformations;

    @Autowired
    public MainContentTocGenerationStrategy(
        final XslTransformationService xslTransformationService,
        final TransformerBuilderFactory transformerBuilderFactory,
        final XppFormatFileSystem xppFormatFileSystem,
        @Value("${xpp.main.content.extract.toc.xsl}") final File extractMainContentTocXsl)
    {
        super(xslTransformationService, transformerBuilderFactory, BundleFileType.MAIN_CONTENT);
        this.xppFormatFileSystem = xppFormatFileSystem;
        this.extractMainContentTocXsl = extractMainContentTocXsl;

        transformations = new TransformationUnit[]{new MainContentTocTransformationUnit()};
    }

    @Override
    protected TransformationUnit[] getTransformationUnits()
    {
        return transformations;
    }

    private class MainContentTocTransformationUnit implements TransformationUnit
    {
        @Override
        public Transformer createTransformer(final XppBundle bundle, final TransformerBuilder builder, final BookStep step)
        {
            return builder.withXsl(extractMainContentTocXsl).build();
        }

        @Override
        public Collection<File> getInputFiles(final String bundleFileName, final XppBundle bundle, final BookStep step)
        {
            return Arrays.asList(xppFormatFileSystem
                .getOriginalFile(step, bundle.getMaterialNumber(), bundleFileName));
        }

        @Override
        public File getOutputFile(final String fileName, final XppBundle bundle, final BookStep step)
        {
            return xppFormatFileSystem
                .getBundlePartTocFile(fileName, bundle.getMaterialNumber(), step);
        }
    }
}
