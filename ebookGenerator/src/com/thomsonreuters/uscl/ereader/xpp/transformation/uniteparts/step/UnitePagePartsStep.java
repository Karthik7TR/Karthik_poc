package com.thomsonreuters.uscl.ereader.xpp.transformation.uniteparts.step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles.DocumentFile;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles.PartFilesByBaseNameIndex;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles.PartFilesByTypeIndex;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles.PartFilesByUuidIndex;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * Unite different parts of original XML page to one page XML
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class UnitePagePartsStep extends XppTransformationStep
{
    @Value("${xpp.unite.page.parts.xsl}")
    private File unitePagePartsXsl;

    @Override
    public void executeTransformation() throws Exception
    {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(unitePagePartsXsl).build();

        for (final Map.Entry<String, PartFilesByBaseNameIndex> fileGroupsByMaterialNumber : fileSystem.getOriginalPartsFiles(this).getPartFilesByMaterialNumber().entrySet())
        {
            final String materialNumber = fileGroupsByMaterialNumber.getKey();
            FileUtils.forceMkdir(fileSystem.getOriginalPagesDirectory(this, materialNumber));
            for (final Map.Entry<String, PartFilesByUuidIndex> pagesGroupedByBaseFileName : fileGroupsByMaterialNumber.getValue().getPartFilesByBaseName().entrySet())
            {
                for (final Map.Entry<String, PartFilesByTypeIndex> pageParts : pagesGroupedByBaseFileName.getValue().getPartFilesByUuid().entrySet())
                {
                    createPage(transformer, materialNumber, pagesGroupedByBaseFileName.getKey(), pageParts.getValue());
                }
            }
        }
    }

    private void createPage(final Transformer transformer, final String materialNumber, final String fileName, final PartFilesByTypeIndex filesByType)
    {
        final Map<PartType, DocumentFile> parts = filesByType.getPartFilesByType();
        final List<File> files = new ArrayList<>();

        final DocumentFile mainFile = parts.get(PartType.MAIN);

        files.add(mainFile.getFile());

        if (parts.get(PartType.FOOTNOTE) != null)
        {
            files.add(parts.get(PartType.FOOTNOTE).getFile());
        }

        transformationService.transform(transformer, files, fileSystem.getOriginalPageFile(this, materialNumber, fileName, mainFile.getDocumentName().getOrder(),
            filesByType.getPartFilesByType().entrySet().iterator().next().getValue().getDocumentName().getDocFamilyUuid()));
    }
}
