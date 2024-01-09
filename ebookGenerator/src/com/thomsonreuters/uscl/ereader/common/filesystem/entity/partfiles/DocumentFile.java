package com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles;

import java.io.File;

import com.thomsonreuters.uscl.ereader.xpp.transformation.generate.title.metadata.step.DocumentName;
import org.jetbrains.annotations.NotNull;

/**
 * Keeps DocumentName with link to file.
 */
public class DocumentFile {
    private DocumentName documentName;
    private File file;

    public DocumentFile(@NotNull final File file) {
        this.file = file;
        documentName = new DocumentName(file.getName());
    }

    @NotNull
    public DocumentName getDocumentName() {
        return documentName;
    }

    @NotNull
    public File getFile() {
        return file;
    }
}
