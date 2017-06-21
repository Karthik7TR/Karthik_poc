package com.thomsonreuters.uscl.ereader.xpp.transformation.move.resources.step;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.sonar.runner.commonsio.FileUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Value;

/**
 * Temporary stem, creates dummy xpp book, valid for proview.
 *
 * created: 05/22/2017
 * Should be removed when our xpp steps will provide us valid assemble.
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class CreateDummyXppBook extends BookStepImpl
{
    @Value("${xpp.static.directory}")
    private File xppStaticDirectory;
    @Resource(name = "assembleFileSystem")
    private AssembleFileSystem assembleFileSystem;

    @Override
    public ExitStatus executeStep() throws Exception
    {
        final File titleDirectory = assembleFileSystem.getTitleDirectory(this);
        FileUtils.forceMkdir(titleDirectory);
        FileUtils.copyDirectory(xppStaticDirectory, titleDirectory);
        prepareTitleXml();

        return ExitStatus.COMPLETED;
    }

    private void prepareTitleXml() throws IOException
    {
        final BookDefinition bookDefinition = getBookDefinition();
        final File titleXml = assembleFileSystem.getTitleXml(this);
        final String titleXmlContent = FileUtils.readFileToString(titleXml)
            .replaceAll("\\$\\{version\\}", getBookVersion().getFullVersion())
            .replaceAll("\\$\\{titleId\\}", bookDefinition.getFullyQualifiedTitleId())
            .replaceAll("\\$\\{ProviewDisplayName\\}", bookDefinition.getProviewDisplayName());
        FileUtils.writeStringToFile(titleXml, titleXmlContent);
    }
}