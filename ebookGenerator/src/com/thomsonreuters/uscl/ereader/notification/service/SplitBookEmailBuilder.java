package com.thomsonreuters.uscl.ereader.notification.service;

import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;

public class SplitBookEmailBuilder extends AbstractEmailBuilder
{
    private static final String SUBJECT_PART = " (Split Book)";

    @Resource(name = "docMetadataService")
    private DocMetadataService docMetadataService;

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.notification.AbstractGeneratorEmailBuilder#getAdditionalSubjectPart()
     */
    @Override
    protected String getAdditionalSubjectPart()
    {
        return SUBJECT_PART;
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.notification.AbstractGeneratorEmailBuilder#getAdditionalBodyPart()
     */
    @Override
    protected String getAdditionalBodyPart()
    {
        final BookDefinition bookDefinition = step.getBookDefinition();
        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final String proviewDisplayName = bookDefinition.getProviewDisplayName();
        final List<String> splitTitles = docMetadataService.findDistinctSplitTitlesByJobId(step.getJobInstanceId());

        final StringBuilder sb = new StringBuilder();
        sb.append("\t\n\t\nPlease find the below information regarding the split titles");
        sb.append("\t\nProview display name : " + proviewDisplayName);
        sb.append("\t\nFully Qualified Title : " + fullyQualifiedTitleId);
        sb.append("\t\nTotal parts : " + splitTitles.size());
        sb.append("\t\nSplit Title Id's :");
        for (final String splitTitleId : splitTitles)
        {
            sb.append("\t\n" + splitTitleId);
        }
        return sb.toString();
    }
}
