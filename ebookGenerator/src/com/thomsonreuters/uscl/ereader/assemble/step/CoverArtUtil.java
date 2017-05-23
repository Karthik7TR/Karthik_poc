package com.thomsonreuters.uscl.ereader.assemble.step;

import java.io.File;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(value = "coverArtUtil")
public class CoverArtUtil
{
    @Value(value = "/apps/eBookBuilder/generator/images/cover/")
    private String defaultCoverPath;
    @Value(value = "coverArt.PNG")
    private String coverFileName;
    @Value(value = "${static.content.dir}")
    private File staticContentDirectory;

    @NotNull
    public File getCoverArt(@NotNull final BookDefinition bookDefinition)
    {
        final String titleCover = bookDefinition.getCoverImage();

        File coverArt = new File(defaultCoverPath + titleCover);
        if (!coverArt.exists())
        {
            coverArt = new File(staticContentDirectory, coverFileName);
        }
        return coverArt;
    }
}
