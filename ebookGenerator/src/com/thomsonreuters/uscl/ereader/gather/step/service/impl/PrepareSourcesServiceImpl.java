package com.thomsonreuters.uscl.ereader.gather.step.service.impl;

import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.gather.step.service.PrepareSourcesService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.File;

import static com.thomsonreuters.uscl.ereader.core.EBConstants.TXT_FILE_EXTENSION;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.XML_FILE_EXTENSION;

@Service
public class PrepareSourcesServiceImpl implements PrepareSourcesService {
    private static final String DIVIDER = "!";

    @Override
    public File getTocFile(final File rootTocFile, final String titleId) {
        return new File(transformFileName(rootTocFile, titleId, XML_FILE_EXTENSION));
    }

    @Override
    public File getDocsGuidsFile(final File rootDocsGuidsFile, final String titleId) {
        return new File(transformFileName(rootDocsGuidsFile, titleId, TXT_FILE_EXTENSION));
    }

    @NotNull
    private String transformFileName(final File rootTocFile, final String titleId, final String fileExtension) {
        return rootTocFile.getAbsolutePath()
                .replace(fileExtension, DIVIDER + new TitleId(titleId).escapeSlashWithDash() + fileExtension);
    }
}
