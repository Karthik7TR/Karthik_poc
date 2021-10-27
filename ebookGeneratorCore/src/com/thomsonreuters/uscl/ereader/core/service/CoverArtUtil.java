package com.thomsonreuters.uscl.ereader.core.service;

import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.DASH;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.TITLE_PAGE_IMAGE;
import static com.thomsonreuters.uscl.ereader.core.FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX;

@Component(value = "coverArtUtil")
public class CoverArtUtil {
    @Value(value = "coverArt.PNG")
    private String coverFileName;
    @Autowired
    private NasFileSystem nasFileSystem;

    @NotNull
    public File getCoverArt(@NotNull final BookDefinition bookDefinition) {
        final String titleCover = bookDefinition.getCoverImage();
        File coverArt = new File(nasFileSystem.getCoverImagesDirectory(), titleCover);
        if (!coverArt.exists()) {
            coverArt = new File(nasFileSystem.getStaticContentDirectory(), coverFileName);
        }
        return coverArt;
    }

    public static String getCoverArtOnProView(@NotNull final BookDefinition bookDefinition) {
        return PROVIEW_ASSERT_REFERENCE_PREFIX + TITLE_PAGE_IMAGE + DASH + new TitleId(bookDefinition.getFullyQualifiedTitleId()).escapeSlashWithDash();
    }

    public static String getCoverArtOnEbookManager(@NotNull final BookDefinition bookDefinition) {
        return CoreConstants.MVC_COVER_IMAGE + "?imageName=" + bookDefinition.getCoverImage();
    }
}
