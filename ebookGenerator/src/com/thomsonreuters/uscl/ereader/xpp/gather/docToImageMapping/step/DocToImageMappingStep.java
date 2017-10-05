package com.thomsonreuters.uscl.ereader.xpp.gather.docToImageMapping.step;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.springframework.beans.factory.annotation.Value;

/**
 * Generates mapping file between documents and containing images.
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class DocToImageMappingStep extends XppTransformationStep {
    @Value("${xpp.document.to.image.map.xsl}")
    private File transformToAnchorToDocumentIdMapXsl;

    @Override
    public void executeTransformation() throws Exception {
        final Transformer transformer =
            transformerBuilderFactory.create().withXsl(transformToAnchorToDocumentIdMapXsl).build();
        final List<File> htmls = new ArrayList<>();
        for (final Map.Entry<String, Collection<File>> materialNumberDir : fileSystem.getExternalLinksFiles(this)
            .entrySet()) {
            htmls.addAll(materialNumberDir.getValue());
        }
        final TransformationCommand command =
            new TransformationCommandBuilder(transformer, fileSystem.getDocToImageMapFile(this)).withInput(htmls)
                .build();
        transformationService.transform(command);
    }
}
