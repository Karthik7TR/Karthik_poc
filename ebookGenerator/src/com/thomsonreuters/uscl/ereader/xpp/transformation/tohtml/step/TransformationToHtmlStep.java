package com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.generate.title.metadata.step.DocumentName;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

/**
 * Creates HTML pages using pages in original XML markup
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class TransformationToHtmlStep extends XppTransformationStep
{
    @Value("${xpp.transform.to.html.xsl}")
    private File transformToHtmlXsl;

    @Override
    public void executeTransformation() throws Exception
    {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(transformToHtmlXsl).build();
        final PagePrefix pagePrefix = new PagePrefix(getXppBundles());
        for (final Map.Entry<String, Collection<File>> dir : fileSystem.getOriginalPageFiles(this).entrySet())
        {
            pagePrefix.switchVolume(dir.getKey());
            FileUtils.forceMkdir(fileSystem.getHtmlPagesDirectory(this, dir.getKey()));
            for (final File part : dir.getValue())
            {
                final String partName = part.getName();
                pagePrefix.switchFileType(partName);
                transformer.setParameter("fileBaseName", FilenameUtils.removeExtension(partName));
                transformer.setParameter("pagePrefix", pagePrefix.getPagePrefix());
                transformer.setParameter("divXmlName", new DocumentName(partName).getBaseName());
                transformationService
                    .transform(transformer, part, fileSystem.getHtmlPageFile(this, dir.getKey(), partName));
            }
        }
    }

    private static final class PagePrefix
    {
        private final Map<String, Integer> volumesNumberMap = new HashMap<>();
        private Integer currentVolume;
        private String currentPrefix;

        private PagePrefix(@NotNull final List<XppBundle> bundles)
        {
            Integer volume = 1;
            for (final XppBundle bundle : bundles)
            {
                volumesNumberMap.put(bundle.getMaterialNumber(), volume++);
            }
        }

        private void switchFileType(@NotNull final String fileName)
        {
            currentPrefix = BundleFileType.getByFileName(fileName).getPagePrefix();
        }

        private void switchVolume(@NotNull final String materialNumber)
        {
            currentVolume = volumesNumberMap.get(materialNumber);
        }

        private String getPagePrefix()
        {
            final StringBuilder builder = new StringBuilder();
            if (volumesNumberMap.size() > 1)
            {
                builder.append("Vol")
                       .append(currentVolume)
                       .append("-");
            }
            if (StringUtils.isNotBlank(currentPrefix))
            {
                builder.append(currentPrefix)
                       .append("-");
            }
            return builder.toString();
        }
    }
}
