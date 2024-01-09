package com.thomsonreuters.uscl.ereader.quality.transformer;

import static org.apache.commons.io.FileUtils.forceDelete;
import static org.apache.commons.lang3.StringUtils.substringBefore;

import java.io.File;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Runs identity transformation on transformed DIVXML files for QualityStep to turn entities into characters
 * For example, "&#38;" will become "&"
 */
@Service
public class IdentityTransformerImpl implements IdentityTransformer {
    private File identityXsl;
    protected XslTransformationService transformationService;
    protected TransformerBuilderFactory transformerBuilderFactory;

    @Autowired
    public IdentityTransformerImpl(
        @Value("${xpp.quality.identity}") final File identityXsl,
        @Qualifier("xslTransformationService") final XslTransformationService transformationService,
        @Qualifier("transformerBuilderFactory") final TransformerBuilderFactory transformerBuilderFactory) {
        this.identityXsl = identityXsl;
        this.transformationService = transformationService;
        this.transformerBuilderFactory = transformerBuilderFactory;
    }

    @Override
    @SneakyThrows
    public void transform(final File file) {
        final String path = file.getAbsolutePath();
        final File tempOutput = new File(path + "_temp");
        final TransformationCommand command =
            new TransformationCommandBuilder(createTransformer(), tempOutput).withInput(file)
                .build();
        transformationService.transform(command);
        forceDelete(file);
        tempOutput.renameTo(new File(substringBefore(path, "_temp")));
    }

    private Transformer createTransformer() {
        return transformerBuilderFactory.create()
            .withXsl(identityXsl)
            .build();
    }
}
