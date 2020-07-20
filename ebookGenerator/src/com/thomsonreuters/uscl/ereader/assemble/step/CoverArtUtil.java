package com.thomsonreuters.uscl.ereader.assemble.step;

import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

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
}
