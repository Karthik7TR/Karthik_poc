package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * Applies any post transformation on the HTML that need to be done to cleanup or make
 * the HTML ProView complient.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public interface HTMLTransformerService
{
    /**
     * This method applies multiple XMLFilters to the source HTML to apply various
     * post transformation rules to the HTML.
     *
     * @param srcDir source directory that contains the html files
     * @param targetDir target directory where the resulting post transformation files are written to
     * @param staticImg target file to which a list of referenced static files will be written out to
     * @param tableViewers will decide to apply TableView or not.
     * @param title title of the book being published
     * @param jobId the job identifier of the current transformation run
     * @param targetAnchors the list of guids with their set of anchors
     * @param docsGuidFile contains the list of doc GUID's that represent the physical docs.
     * @param deDuppingFile target file where dedupping anchors are updated.
     * @param isHighlight setting to enable light blue highlighting on text for ins HTML tags
     * @param isStrikethrough setting to enable strike-through on text for del HTML tags
     * @param delEditorNodeHeading setting to remove HTML tags and text of Editors' Notes
     *
     * @return the number of documents that had post transformations run on them
     *
     * @throws if no source files are found or any parsing/transformation exception are encountered
     */
    int transformHTML(
        File srcDir,
        File targetDir,
        File staticImg,
        List<TableViewer> tableViewers,
        String title,
        Long jobId,
        Map<String, Set<String>> targetAnchors,
        File docsGuidFile,
        File deDuppingFile,
        boolean isHighlight,
        boolean isStrikethrough,
        boolean delEditorNodeHeading,
        String version) throws EBookFormatException;
}
