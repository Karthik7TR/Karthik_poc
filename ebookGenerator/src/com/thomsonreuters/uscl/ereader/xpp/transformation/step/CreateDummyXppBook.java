package com.thomsonreuters.uscl.ereader.xpp.transformation.step;

import java.io.File;
import java.io.IOException;

import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatus;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.xpp.common.XppBookStep;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Value;

@SavePublishingStatus
public class CreateDummyXppBook extends BookStepImpl implements XppBookStep
{
    @Value("${xpp.static.directory}")
    private File xppStaticDirectory;

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.common.step.BaseStep#executeStep()
     */
    @Override
    public ExitStatus executeStep() throws Exception
    {
        final File assembleDirectory = getAssembleDirectory();
        assembleDirectory.mkdir();

        FileUtils.copyDirectory(xppStaticDirectory, assembleDirectory);
        prepareTitleXml();

        return ExitStatus.COMPLETED;
    }

    private void prepareTitleXml() throws IOException
    {
        final BookDefinition bookDefinition = getBookDefinition();
        final File titleXml = getTitleXml();
        String titleXmlContent = FileUtils.readFileToString(titleXml);
        titleXmlContent = titleXmlContent.replaceAll("\\$\\{version\\}", getBookVersion());
        titleXmlContent = titleXmlContent.replaceAll("\\$\\{titleId\\}", bookDefinition.getFullyQualifiedTitleId());
        titleXmlContent =
            titleXmlContent.replaceAll("\\$\\{ProviewDisplayName\\}", bookDefinition.getProviewDisplayName());
        FileUtils.writeStringToFile(titleXml, titleXmlContent);
    }

    void setXppStaticDirectory(final File xppStaticDirectory)
    {
        this.xppStaticDirectory = xppStaticDirectory;
    }
}
