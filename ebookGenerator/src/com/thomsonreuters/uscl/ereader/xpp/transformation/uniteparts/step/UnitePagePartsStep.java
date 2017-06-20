package com.thomsonreuters.uscl.ereader.xpp.transformation.uniteparts.step;

import static java.util.Arrays.asList;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.filesystem.entity.PartFilesByBaseNameIndex;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.PartFilesByOrderIndex;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.PartFilesByTypeIndex;
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
            for (final Map.Entry<String, PartFilesByOrderIndex> pagesGroupedByBaseFileName : fileGroupsByMaterialNumber.getValue().getPartFilesByBaseName().entrySet())
            {
                for (final Map.Entry<Integer, PartFilesByTypeIndex> pageParts : pagesGroupedByBaseFileName.getValue().getPartFilesByOrder().entrySet())
                {
                    createPage(transformer, materialNumber, pagesGroupedByBaseFileName.getKey(), pageParts.getKey(), pageParts.getValue());
                }
            }
        }
    }

    private void createPage(final Transformer transformer, final String materialNumber, final String fileName, final Integer index, final PartFilesByTypeIndex filesByType)
    {
        final File mainPart = filesByType.getPartFilesByType().get(PartType.MAIN).getFile();
        //TODO: return back when split by structure for footnotes is ready
        //final File footnotesPart = filesByType.getPartFilesByType().get(PartType.FOOTNOTE).getFile();
        final List<File> files = asList(mainPart);

        transformationService.transform(transformer, files, fileSystem.getOriginalPageFile(this, materialNumber, fileName, index,
            filesByType.getPartFilesByType().entrySet().iterator().next().getValue().getDocumentName().getDocFamilyGuid()));
    }
}
